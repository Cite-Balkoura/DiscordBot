package fr.milekat.discordbot.bot.master.features;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.classes.Registration;
import fr.milekat.discordbot.bot.master.classes.Step;
import fr.milekat.discordbot.bot.master.classes.StepInput;
import fr.milekat.discordbot.bot.master.managers.ProfileManager;
import fr.milekat.discordbot.bot.master.managers.RegistrationManager;
import fr.milekat.discordbot.bot.master.managers.StepManager;
import fr.milekat.discordbot.utils.MojangNames;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenuInteraction;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProfileRegister extends ListenerAdapter {
    /**
     * Add button on new message in Event channel
     */
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (RegistrationManager.isRegistration(event.getChannel().getIdLong()) && !event.getAuthor().isBot()) {
            //  TEXT step update
            formStepReceive(event.getMessage(), event.getMember(),
                    RegistrationManager.getRegistration(event.getMember().getIdLong()), null, null);
            return;
        }
        if (event.getAuthor().isBot() || !event.getChannel().equals(BotUtils.getChannel("cRegister"))) return;
        event.getChannel().sendMessage(event.getMessage().getContentRaw()).setActionRow(Button.success(event.getAuthor().getName(), BotUtils.getMsg("profileReg.buttonFrom")).withEmoji(Emoji.fromUnicode("\uD83D\uDCDD"))).queue();
        event.getMessage().delete().queue();
    }

    /**
     * When a member of guild click on buttonAccountRegister to ask to register his profile
     */
    @Override
    public void onButtonClick(@Nonnull ButtonClickEvent event) {
        if (RegistrationManager.isRegistration(event.getTextChannel().getIdLong())) {
            //  VALID OR FINAL step update
            formStepReceive(null, event.getMember(), RegistrationManager.getRegistration(event.getMember().getIdLong()),
                    event.getButton(), null);
            return;
        }
        if (!event.getChannel().equals(BotUtils.getChannel("cRegister"))) return;
        if (event.getMember().getRoles().contains(BotUtils.getRole("rProfile")) ||
                event.getMember().getRoles().contains(BotUtils.getRole("rWaiting"))) {
            BotUtils.reply(event, "profileReg.rulesAlreadyAccepted");
            return;
        }
        //  Add "rWaiting" role to user
        event.getGuild().addRoleToMember(event.getMember(), BotUtils.getRole("rWaiting")).queue();
        //  Start form for user !
        openForm(event);
    }

    /**
     * CHOICE step update
     */
    @Override
    public void onSelectionMenu(@Nonnull SelectionMenuEvent event) {
        if (RegistrationManager.isRegistration(event.getTextChannel().getIdLong())) formStepReceive(null, event.getMember(),
                RegistrationManager.getRegistration(event.getMember().getIdLong()), null, event.getInteraction());
    }

    /**
     * Open a new form (Ticket)
     */
    private void openForm(GenericComponentInteractionCreateEvent event) {
        BotUtils.getCategory("ccRegister").createTextChannel(event.getMember().getEffectiveName()).queue((textChannel) -> {
            //  Add user to channel
            textChannel.putPermissionOverride(event.getMember()).setAllow(Permission.VIEW_CHANNEL).queue();
            //  Notify user of channel creation
            BotUtils.reply(event, "profileReg.formOpenConfirm", Collections.singletonMap("#channel", textChannel.getAsMention()));
            Registration registration = new Registration(event.getMember().getIdLong(), textChannel.getIdLong());
            RegistrationManager.save(registration);
            formStepSend(event.getMember(), registration);
        });
    }

    /**
     * Send the new step message to user in channel
     */
    private void formStepSend(Member member, Registration registration) {
        if (!StepManager.exists(registration.getStep())) {
            if (Main.DEBUG_ERROR) Main.log("[" + member.getAsMention() + "] Unregister step: " + registration.getStep());
            return;
        }
        Step step = StepManager.getStep(registration.getStep());
        if (Main.DEBUG_ERROR) Main.log("[" + member.getAsMention() + "] Send step: " + step.getName());
        switch (step.getType()) {
            case TEXT -> registration.getChannel().sendMessageEmbeds(getTextEmbed(member, step).build()).queue();
            case VALID -> {
                if (step.getName().equalsIgnoreCase("Skin")) {  //  Skin step exception
                    BotUtils.sendRegister(member, getSkinEmbed(member, step, registration).build());
                } else {
                    BotUtils.sendRegister(member, getBasicEmbed(member, step).build());
                }
            }
            case CHOICES -> registration.getChannel().sendMessageEmbeds(getBasicEmbed(member, step).build()).setActionRow(
                    SelectionMenu.create(step.getName())
                            .addOptions(step.getChoices().stream().map(choice ->
                                    SelectOption.of(choice, choice)).collect(Collectors.toList()))
                            .setMaxValues(step.getChoices().size())
                            .build()
                    ).queue();
            case FINAL -> {
                EmbedBuilder builder = getFinalEmbed(member, step, registration);
                builder.setDescription(step.getAnswer());
                BotUtils.sendRegister(member, builder.build());
            }
            default -> {
                if (Main.DEBUG_ERROR) Main.log("Unknown step: " + step.getType());
            }
        }
    }

    /**
     * Execute actions for the current step of user
     */
    private void formStepReceive(Message message, Member member, Registration registration, Button button, SelectionMenuInteraction selection) {
        // TODO: 29/07/2021 Remove both user msg if exist, and bot message ! To prevent multiple reply
        if (registration == null) {
            if (Main.DEBUG_ERROR) Main.log("[" + member.getAsMention() + "] Player null");
            return;
        }
        if (registration.getDiscordId() != member.getIdLong()) {
            if (Main.DEBUG_ERROR) Main.log("[" + member.getAsMention() + "] Id : " + registration.getDiscordId() + " / " + member.getIdLong());
            return;
        }
        if (StepManager.exists(registration.getStep())) {
            //  Player is in form register
            Step step = StepManager.getStep(registration.getStep());
            if (Main.DEBUG_ERROR) Main.log("[" + member.getAsMention() + "] Receive step: " + step.getName());
            switch (step.getType()) {
                case TEXT -> {
                    if (step.getMin() <= message.getContentRaw().length() && step.getMax() >= message.getContentRaw().length()) {
                        if (step.getName().equalsIgnoreCase("Pseudo Mc")) { //  Username exception
                            if (ProfileManager.exists(message.getContentRaw())) {
                                BotUtils.sendRegister(member, "profileReg.formFieldError");
                            } else {
                                try {
                                    registration.setUsername(message.getContentRaw());
                                    registration.setUuid(UUID.fromString(MojangNames.getUuid(message.getContentRaw())));
                                } catch (IOException ignored) {
                                    BotUtils.registerAdminAssist(member, "Mojang data error please retry.");
                                    if (Main.DEBUG_ERROR)
                                        Main.log("[" + member.getAsMention() + "] Mojang data error, retry..");
                                    formStepSend(member, registration);
                                    return;
                                }
                            }
                        }
                        if (step.isSave()) registration.addInputs(new StepInput(step, message.getContentRaw()));
                    } else {
                        BotUtils.sendRegister(member, BotUtils.getMsg("profileReg.formFieldError"));
                    }
                }
                case VALID -> {
                    if (button.getLabel().equalsIgnoreCase("yes")) {
                        BotUtils.sendRegister(member, step.getYes());
                        registration.setStep(step.getNext());
                        if (step.isSave()) registration.addInputs(new StepInput(step, button.getLabel()));
                    } else if (button.getLabel().equalsIgnoreCase("no")) {
                        BotUtils.sendRegister(member, step.getNo());
                        registration.setStep(step.getReturnStep());
                    } else {
                        BotUtils.registerAdminAssist(member, "Responses unknown ?");
                    }
                }
                case CHOICES -> {
                    registration.setStep(step.getNext());
                    if (step.isSave()) registration.addInputs(new StepInput(step, selection.getSelectedOptions()
                            .stream()
                            .map(SelectOption::getDescription)
                            .collect(Collectors.joining(", "))));
                }
                case FINAL -> {
                    if (button.getLabel().equalsIgnoreCase("yes")) {
                        // TODO: 29/07/2021 Send form to staff
                        //manager.sendEmbed(cCandid, getFinalEmbed(user, player).build());
                        BotUtils.sendRegister(member, step.getYes());
                        registration.setStep("WAITING");
                        BotUtils.getGuild().addRoleToMember(member, BotUtils.getRole("rWaiting")).queue();
                        return; //  End of from !
                        // TODO: 29/07/2021 Properly end the form
                    } else if (button.getLabel().equalsIgnoreCase("no")) {
                        BotUtils.sendRegister(member, step.getNo());
                        registration.setStep(step.getReturnStep());
                    } else {
                        BotUtils.registerAdminAssist(member, "Responses unknown ?");
                    }
                }
            }
            RegistrationManager.save(registration);
            formStepSend(member, registration);
        } else if (registration.getStep().equals("REFUSED")) {
            // TODO: 28/07/2021 Notify player he is refused
        } else {
            // TODO: 28/07/2021 Send DATA ERROR
        }
    }

    /**
     * Get a basic embed
     */
    private EmbedBuilder getBasicEmbed(Member member, Step step) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.BLUE).setDescription(BotUtils.setNick(member, step.getAnswer()));
        return builder;
    }

    /**
     * Get an embed for a TEXT step (= Basic with Min/Max fields)
     */
    private EmbedBuilder getTextEmbed(Member member, Step step) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.BLUE).setDescription(BotUtils.setNick(member, step.getAnswer()))
                .addField("Min chars", String.valueOf(step.getMin()), true)
                .addField("Max chars", String.valueOf(step.getMax()), true);
        return builder;
    }

    /**
     * Get an embed with skin of player (= Basic with skin Image)
     */
    private EmbedBuilder getSkinEmbed(Member member, Step step, Registration registration) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.BLUE).setDescription(BotUtils.setNick(member, step.getAnswer())).setImage(
                "https://crafatar.com/renders/body/" + registration.getUuid().toString() + "?size=512&overlay&default=MHF_Alex");
        return builder;
    }

    /**
     * Get an embed with full responses of player
     */
    private EmbedBuilder getFinalEmbed(Member member, Step step, Registration registration) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.red).setDescription(BotUtils.setNick(member, step.getAnswer())).setThumbnail(
                "https://crafatar.com/renders/body/" + registration.getUuid().toString() + "?size=512&overlay&default=MHF_Alex");
        for (StepInput input : registration.getInputs()) {
            builder.addField(":question: " + input.getStep(), ":arrow_right: " + input.getAnswer() + "\n", false);
        }
        builder.setFooter(String.valueOf(registration.getDiscordId()));
        return builder;
    }
}
