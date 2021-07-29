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
import java.util.*;
import java.util.stream.Collectors;

public class ProfileRegister extends ListenerAdapter {
    /**
     * Add button on new message in Event channel
     */
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (RegistrationManager.isRegistration(event.getChannel().getIdLong()) && !event.getAuthor().isBot()) {
            Registration registration = RegistrationManager.getRegistration(Objects.requireNonNull(event.getMember()).getIdLong());
            if (!event.getMessage().getMentionedRoles().isEmpty() || !event.getMessage().getMentionedMembers().isEmpty()
                    || registration==null || !event.getChannel().equals(registration.getChannel())) return;
            //  TEXT step update
            formStepReceive(event.getMessage(), event.getMember(),
                    RegistrationManager.getRegistration(event.getMember().getIdLong()), null, null, null);
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
            Registration registration = RegistrationManager.getRegistration(Objects.requireNonNull(event.getMember()).getIdLong());
            if (registration==null || !event.getChannel().equals(registration.getChannel())) return;
            //  VALID OR FINAL step update
            formStepReceive(event.getMessage(), event.getMember(),
                    RegistrationManager.getRegistration(event.getMember().getIdLong()), event.getButton(), null, event);
            return;
        }
        if (!event.getChannel().equals(BotUtils.getChannel("cRegister"))) return;
        if (Objects.requireNonNull(event.getMember()).getRoles().contains(BotUtils.getRole("rProfile")) ||
                event.getMember().getRoles().contains(BotUtils.getRole("rWaiting"))) {
            BotUtils.reply(event, "profileReg.rulesAlreadyAccepted");
            return;
        }
        //  Add "rWaiting" role to user
        BotUtils.getGuild().addRoleToMember(event.getMember(), BotUtils.getRole("rWaiting")).queue();
        //  Start form for user !
        openForm(event);
    }

    /**
     * CHOICE step update
     */
    @Override
    public void onSelectionMenu(@Nonnull SelectionMenuEvent event) {
        if (RegistrationManager.isRegistration(event.getTextChannel().getIdLong())) {
            Registration registration = RegistrationManager.getRegistration(Objects.requireNonNull(event.getMember()).getIdLong());
            if (registration==null || !event.getChannel().equals(registration.getChannel())) return;
            formStepReceive(event.getMessage(), event.getMember(),
                    RegistrationManager.getRegistration(event.getMember().getIdLong()), null, event.getInteraction(), event);
        }
    }

    /**
     * Open a new form (Ticket)
     */
    private void openForm(GenericComponentInteractionCreateEvent event) {
        BotUtils.getCategory("ccRegister").createTextChannel(event.getUser().getName()).queue((textChannel) -> {
            //  Add user to channel
            textChannel.putPermissionOverride(Objects.requireNonNull(event.getMember())).setAllow(Permission.VIEW_CHANNEL).queue();
            //  Notify user of channel creation
            BotUtils.reply(event, "profileReg.formOpenConfirm", Collections.singletonMap("#channel", textChannel.getAsMention()));
            Registration registration = new Registration(event.getMember().getIdLong(), textChannel.getIdLong());
            RegistrationManager.save(registration);
            Main.getJDA().getTextChannelById(textChannel.getIdLong()).sendTyping().queue();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    formStepSend(event.getMember(), registration);
                }
            }, 1000L);
        });
    }

    /**
     * Send the new step message to user in channel
     */
    private void formStepSend(Member member, Registration registration) {
        if (!StepManager.exists(registration.getStep())) {
            if (Main.DEBUG_ERROR) Main.log("[" + member.getUser().getAsTag() + "] Unregister step: " + registration.getStep());
            return;
        }
        Step step = StepManager.getStep(registration.getStep());
        if (Main.DEBUG_ERROR) Main.log("[" + member.getUser().getAsTag() + "] Send step: " + step.getName());
        switch (step.getType()) {
            case TEXT -> registration.getChannel().sendMessageEmbeds(getMinMaxEmbed(member, step).build()).queue();
            case VALID -> {
                if (step.getName().equalsIgnoreCase("Skin")) {  //  Skin step exception
                    BotUtils.sendRegister(member, getSkinEmbed(member, step, registration).build());
                } else {
                    BotUtils.sendRegister(member, getBasicEmbed(member, step).build());
                }
            }
            case CHOICES -> registration.getChannel().sendMessageEmbeds(getMinMaxEmbed(member, step).build()).setActionRow(
                    SelectionMenu.create(step.getName())
                            .addOptions(step.getChoices().stream().map(choice ->
                                    SelectOption.of(choice, choice)).collect(Collectors.toList()))
                            .setMinValues(step.getMin())
                            .setMaxValues(step.getMax())
                            .build()
                    ).queue();
            case FINAL -> {
                EmbedBuilder builder = getFinalEmbed(member, step, registration);
                builder.setDescription(step.getQuestion());
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
    private void formStepReceive(Message message, Member member, Registration registration, Button button, SelectionMenuInteraction selection, GenericComponentInteractionCreateEvent event) {
        // TODO: 29/07/2021 Remove both user msg if exist, and bot message ! To prevent multiple reply
        if (registration == null) {
            if (Main.DEBUG_ERROR) Main.log("[" + member.getUser().getAsTag() + "] Player null");
            if (event!=null) event.reply("Error").queue(interactionHook -> interactionHook.deleteOriginal().queue());
            return;
        }
        if (registration.getDiscordId() != member.getIdLong()) {
            if (Main.DEBUG_ERROR) Main.log("[" + member.getUser().getAsTag() + "] Id : " + registration.getDiscordId() + " / " + member.getIdLong());
            if (event!=null) event.reply("Error").queue(interactionHook -> interactionHook.deleteOriginal().queue());
            return;
        }
        if (StepManager.exists(registration.getStep())) {
            //  Player is in form register
            Step step = StepManager.getStep(registration.getStep());
            if (Main.DEBUG_ERROR) Main.log("[" + member.getUser().getAsTag() + "] Receive step: " + step.getName());
            switch (step.getType()) {
                case TEXT -> {
                    if (message==null) return;
                    if (step.getMin() <= message.getContentRaw().length() && step.getMax() >= message.getContentRaw().length()) {
                        if (step.getName().equalsIgnoreCase("Pseudo Mc")) { //  Username exception
                            if (ProfileManager.exists(message.getContentRaw())) {
                                BotUtils.sendRegister(member, BotUtils.getMsg("profileReg.formFieldError"));
                            } else {
                                try {
                                    registration.setUsername(message.getContentRaw());
                                    registration.setUuid(UUID.fromString(MojangNames.getUuid(message.getContentRaw())));
                                    registration.setStep(step.getNext());
                                    if (step.isSave()) registration.addInputs(new StepInput(step, message.getContentRaw()));
                                } catch (IOException ignored) {
                                    BotUtils.registerAdminAssist(member, "Mojang data error please retry.");
                                    if (Main.DEBUG_ERROR) Main.log("[" + member.getAsMention() + "] Mojang data error, retry..");
                                    return;
                                } catch (IllegalArgumentException ignored) {
                                    BotUtils.sendRegister(member, BotUtils.getMsg("profileReg.formFieldError"));
                                    return;
                                }
                            }
                        } else {
                            registration.setStep(step.getNext());
                            if (step.isSave()) registration.addInputs(new StepInput(step, message.getContentRaw()));
                        }
                    } else {
                        BotUtils.sendRegister(member, BotUtils.getMsg("profileReg.formFieldError"));
                    }
                }
                case VALID -> {
                    if (button==null || button.getId()==null || button.getEmoji()==null) return;
                    message.editMessageComponents().setActionRows().queue();
                    if (button.getId().equalsIgnoreCase("yes")) {
                        event.reply(button.getEmoji().getAsMention() + step.getYes()).queue();
                        registration.setStep(step.getNext());
                        if (step.isSave()) registration.addInputs(new StepInput(step, button.getLabel()));
                    } else if (button.getId().equalsIgnoreCase("no")) {
                        event.reply(button.getEmoji().getAsMention() + step.getNo()).queue();
                        registration.setStep(step.getReturnStep());
                    } else {
                        BotUtils.registerAdminAssist(member, "Responses unknown ?");
                    }
                }
                case CHOICES -> {
                    if (selection==null || selection.getSelectedOptions()==null) return;
                    message.editMessageComponents().setActionRows().queue();
                    String choices = selection.getSelectedOptions()
                            .stream()
                            .map(SelectOption::getLabel)
                            .collect(Collectors.joining(", "));
                    event.reply(choices).queue();
                    registration.setStep(step.getNext());
                    if (step.isSave()) registration.addInputs(new StepInput(step, choices));
                }
                case FINAL -> {
                    if (button==null || button.getId()==null || button.getEmoji()==null) return;
                    message.editMessageComponents().setActionRows().queue();
                    if (button.getId().equalsIgnoreCase("yes")) {
                        // TODO: 29/07/2021 Send form to staff
                        //manager.sendEmbed(cCandid, getFinalEmbed(user, player).build());
                        event.reply(button.getEmoji().getAsMention() + step.getYes()).queue();
                        registration.setStep("WAITING");
                        BotUtils.getGuild().addRoleToMember(member, BotUtils.getRole("rWaiting")).queue();
                        return; //  End of from !
                        // TODO: 29/07/2021 Properly end the form
                    } else if (button.getId().equalsIgnoreCase("no")) {
                        event.reply(button.getEmoji().getAsMention() + step.getNo()).queue();
                        registration.setStep(step.getReturnStep());
                    } else {
                        BotUtils.registerAdminAssist(member, "Responses unknown ?");
                    }
                }
            }
            RegistrationManager.save(registration);
            Main.getJDA().getTextChannelById(registration.getChannel().getIdLong()).sendTyping().queue();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    formStepSend(member, registration);
                }
            }, 1000L);
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
        builder.setColor(Color.BLUE).setDescription(BotUtils.setNick(member, step.getQuestion()));
        return builder;
    }

    /**
     * Get an embed for a TEXT step (= Basic with Min/Max fields)
     */
    private EmbedBuilder getMinMaxEmbed(Member member, Step step) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.BLUE).setDescription(BotUtils.setNick(member, step.getQuestion()))
                .addField("Min", String.valueOf(step.getMin()), true)
                .addField("Max", String.valueOf(step.getMax()), true);
        return builder;
    }

    /**
     * Get an embed with skin of player (= Basic with skin Image)
     */
    private EmbedBuilder getSkinEmbed(Member member, Step step, Registration registration) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.BLUE).setDescription(BotUtils.setNick(member, step.getQuestion())).setImage(
                "https://crafatar.com/renders/body/" + registration.getUuid().toString() + "?size=512&overlay&default=MHF_Alex");
        return builder;
    }

    /**
     * Get an embed with full responses of player
     */
    private EmbedBuilder getFinalEmbed(Member member, Step step, Registration registration) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.red).setDescription(BotUtils.setNick(member, step.getQuestion())).setThumbnail(
                "https://crafatar.com/renders/body/" + registration.getUuid().toString() + "?size=512&overlay&default=MHF_Alex");
        for (StepInput input : registration.getInputs()) {
            builder.addField(":question: " + input.getStep().getName(), ":arrow_right: " + input.getAnswer() + "\n", false);
        }
        builder.setFooter(String.valueOf(registration.getDiscordId()));
        return builder;
    }
}
