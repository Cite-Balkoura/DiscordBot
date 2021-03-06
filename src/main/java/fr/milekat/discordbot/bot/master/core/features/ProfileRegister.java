package fr.milekat.discordbot.bot.master.core.features;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import fr.milekat.discordbot.bot.master.core.classes.Registration;
import fr.milekat.discordbot.bot.master.core.classes.Step;
import fr.milekat.discordbot.bot.master.core.classes.StepInput;
import fr.milekat.discordbot.bot.master.core.managers.ProfileManager;
import fr.milekat.discordbot.bot.master.core.managers.RegistrationManager;
import fr.milekat.discordbot.bot.master.core.managers.StepManager;
import fr.milekat.utils.McNames;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
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
    public ProfileRegister() {
        BotUtils.getGuild().upsertCommand(
                new CommandData("register", BotUtils.getMsg("profileReg.slashRegister"))
                        .addOptions(new OptionData(OptionType.STRING,
                                        "action",
                                        BotUtils.getMsg("profileReg.slashOptDescAction"),
                                        true).addChoice("set-step", "set-step"),
                                new OptionData(OptionType.STRING,
                                        "name",
                                        BotUtils.getMsg("profileReg.slashOptDescStepName"),
                                        true).addChoices(StepManager.getSteps().stream()
                                        .map(step -> new Command.Choice(step.getName(), step.getName()))
                                        .toList()
                                )
                        ).setDefaultEnabled(false)
        ).queue(command -> BotUtils.getGuild().updateCommandPrivilegesById(command.getIdLong(),
                new CommandPrivilege(CommandPrivilege.Type.ROLE, true, BotUtils.getRole("rAdmin").getIdLong())
        ).queue());
        BotUtils.getGuild().upsertCommand(
                new CommandData("open-reg", "Open registrations").setDefaultEnabled(false)
        ).queue(command -> BotUtils.getGuild().updateCommandPrivilegesById(command.getIdLong(),
                new CommandPrivilege(CommandPrivilege.Type.ROLE, true, BotUtils.getRole("rAdmin").getIdLong())
        ).queue());
    }

    /**
     * Set step command
     */
    @Override
    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        if (event.getUser().isBot() || !event.getGuild().equals(BotUtils.getGuild())) return;
        if (event.getName().equalsIgnoreCase("register")) {
            if (!event.getMember().getRoles().contains(BotUtils.getRole("rAdmin"))) {
                event.reply(BotUtils.getMsg("noPerm")).setEphemeral(true).queue();
                return;
            }
            if (!RegistrationManager.isRegistration(event.getChannel().getIdLong())) {
                event.reply("Bad channel").setEphemeral(true).queue();
                return;
            }
            Registration registration = RegistrationManager.getRegistrationByChannel(event.getTextChannel().getIdLong());
            if (event.getOption("action") != null && event.getOption("name") != null &&
                    event.getOption("action").getAsString().equalsIgnoreCase("set-step")) {
                registration.setStep(event.getOption("name").getAsString());
                RegistrationManager.save(registration);
                BotUtils.getGuild().retrieveMemberById(registration.getDiscordId()).queue(member -> formStepSend(member, registration));
            }
            event.reply("Done !").setEphemeral(true).queue();
        } else if (event.getName().equalsIgnoreCase("open-reg")) {
            if (!event.getMember().getRoles().contains(BotUtils.getRole("rAdmin"))) {
                event.reply(BotUtils.getMsg("noPerm")).setEphemeral(true).queue();
                return;
            }
            BotUtils.getChannel("cRegister").sendMessage(BotUtils.getMsg("profileReg.registrationMessage"))
                    .setActionRow(Button.success("register", BotUtils.getMsg("profileReg.buttonFrom"))
                            .withEmoji(Emoji.fromUnicode("\uD83D\uDCDD"))).queue();
            event.reply(BotUtils.getChannel("cRegister").getAsMention()).setEphemeral(true).queue();
        }
    }

    /**
     * Add button on new message in Event channel
     */
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (RegistrationManager.isRegistration(event.getChannel().getIdLong()) && !event.getAuthor().isBot()) {
            Registration registration = RegistrationManager.getRegistration(event.getMember().getIdLong());
            if (!event.getMessage().getMentionedRoles().isEmpty() || !event.getMessage().getMentionedMembers().isEmpty()
                    || registration==null || !event.getChannel().equals(registration.getChannel())) return;
            //  TEXT step update
            formStepReceive(event.getMessage(), event.getMember(),
                    RegistrationManager.getRegistration(event.getMember().getIdLong()), null, null, null);
        }
    }

    /**
     * When a member of guild click on buttonAccountRegister to ask to register his profile
     */
    @Override
    public void onButtonClick(@Nonnull ButtonClickEvent event) {
        if (event.getMember()==null || event.getButton()==null) return;
        if (event.getChannel().equals(BotUtils.getChannel("cRegister"))) {
            if (event.getMember().getRoles().contains(BotUtils.getRole("rProfile"))) {
                BotUtils.reply(event, "profileReg.rulesAlreadyRegistered");
                return;
            }
            if(event.getMember().getRoles().contains(BotUtils.getRole("rWaiting"))) {
                BotUtils.reply(event, "profileReg.rulesAlreadyAccepted");
                return;
            }
            //  Add "rWaiting" role to user
            BotUtils.getGuild().addRoleToMember(event.getMember(), BotUtils.getRole("rWaiting")).queue();
            //  Start form for user !
            openForm(event);
        } else if (event.getChannel().equals(BotUtils.getChannel("cStaffValidation"))) {
            if (event.getMessage()==null || event.getMember()==null || event.getButton()==null) return;
            staffVote(event.getMessage(), event.getMember(), event.getButton());
            event.reply("Voted !").setEphemeral(true).queue();
        } else if (RegistrationManager.isRegistration(event.getTextChannel().getIdLong())) {
            Registration registration = RegistrationManager.getRegistration(Objects.requireNonNull(event.getMember()).getIdLong());
            if (registration==null || !event.getChannel().equals(registration.getChannel())) return;
            if (event.getButton().getId().equalsIgnoreCase("regAcknowledge")) {
                event.reply("bey").setEphemeral(true).queue();
                registration.getChannel().delete().reason("Remove button click by " + event.getUser().getAsTag()).queue();
            } else {
                //  VALID OR FINAL step update
                formStepReceive(event.getMessage(), event.getMember(),
                        RegistrationManager.getRegistration(event.getMember().getIdLong()), event.getButton(), null, event);
            }
        }
    }

    /**
     * CHOICE step update
     */
    @Override
    public void onSelectionMenu(@Nonnull SelectionMenuEvent event) {
        if (RegistrationManager.isRegistration(event.getTextChannel().getIdLong())) {
            Registration registration = RegistrationManager.getRegistration(Objects.requireNonNull(event.getMember()).getIdLong());
            if (registration==null || !event.getChannel().equals(registration.getChannel())) return;
            //  CHOICE
            formStepReceive(event.getMessage(), event.getMember(),
                    RegistrationManager.getRegistration(event.getMember().getIdLong()), null, event.getInteraction(), event);
        }
    }

    /**
     * Open a new form (Ticket)
     */
    private void openForm(GenericComponentInteractionCreateEvent event) {
        BotUtils.getCategory("ccRegister").createTextChannel(event.getMember().getEffectiveName()).queue((textChannel) -> {
            //  Add user to channel
            textChannel.putPermissionOverride(Objects.requireNonNull(event.getMember())).setAllow(Permission.VIEW_CHANNEL).queue();
            //  Notify user of channel creation
            BotUtils.reply(event, "profileReg.formOpenConfirm", Collections.singletonMap("#channel", textChannel.getAsMention()));
            if (RegistrationManager.exists(event.getUser().getIdLong())) RegistrationManager.delete(event.getUser().getIdLong());
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
            if (Main.DEBUG_ERRORS) Main.log("[" + member.getUser().getAsTag() + "] Unregister step: " + registration.getStep());
            return;
        }
        Step step = StepManager.getStep(registration.getStep());
        if (Main.DEBUG_ERRORS) Main.log("[" + member.getUser().getAsTag() + "] Send step: " + step.getName());
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
                EmbedBuilder builder = getFinalEmbed(registration, step.getQuestion());
                builder.setDescription(step.getQuestion());
                BotUtils.sendRegister(member, builder.build());
            }
            default -> {
                if (Main.DEBUG_ERRORS) Main.log("Unknown step: " + step.getType());
            }
        }
    }

    /**
     * Execute actions for the current step of user
     */
    private void formStepReceive(Message message, Member member, Registration registration, Button button,
                                 SelectionMenuInteraction selection, GenericComponentInteractionCreateEvent event) {
        if (registration == null) {
            if (Main.DEBUG_ERRORS) Main.log("[" + member.getUser().getAsTag() + "] Player null");
            if (event!=null) event.reply("Error").queue(interactionHook -> interactionHook.deleteOriginal().queue());
            return;
        }
        if (registration.getDiscordId() != member.getIdLong()) {
            if (Main.DEBUG_ERRORS) Main.log("[" + member.getUser().getAsTag() + "] Id : " +
                    registration.getDiscordId() + " / " + member.getIdLong());
            if (event!=null) event.reply("Error").queue(interactionHook -> interactionHook.deleteOriginal().queue());
            return;
        }
        if (StepManager.exists(registration.getStep())) {
            //  Player is in form register
            Step step = StepManager.getStep(registration.getStep());
            if (Main.DEBUG_ERRORS) Main.log("[" + member.getUser().getAsTag() + "] Receive step: " + step.getName());
            switch (step.getType()) {
                case TEXT -> {
                    if (message == null) return;
                    if (step.getMin() <= message.getContentRaw().length() && step.getMax() >= message.getContentRaw().length()) {
                        if (step.getName().equalsIgnoreCase("Pseudo Mc")) { //  Username exception
                            if (ProfileManager.exists(message.getContentRaw())) {
                                BotUtils.sendRegister(member, BotUtils.getMsg("profileReg.formFieldError"));
                            } else {
                                try {
                                    registration.setUsername(message.getContentRaw());
                                    registration.setUuid(UUID.fromString(McNames.getUuid(message.getContentRaw())));
                                    registration.setStep(step.getNext());
                                    if (step.isSave()) registration.addInputs(new StepInput(step, message.getContentRaw()));
                                } catch (IOException ignored) {
                                    BotUtils.registerAdminAssist(member, "Mojang data error please retry.");
                                    if (Main.DEBUG_ERRORS) Main.log("[" + member.getAsMention() + "] Mojang data error, retry..");
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
                    if (button == null || button.getId() == null || button.getEmoji() == null) return;
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
                    if (selection == null || selection.getSelectedOptions() == null) return;
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
                    if (button == null || button.getId() == null || button.getEmoji() == null) return;
                    message.editMessageComponents().setActionRows().queue();
                    if (button.getId().equalsIgnoreCase("yes")) {
                        BotUtils.getChannel("cStaffValidation")
                                .sendMessageEmbeds(getFinalEmbed(registration, BotUtils.getMsg("profileReg.staffNewForm")).build())
                                .setActionRow(Button.success("yes", Emoji.fromMarkdown("<a:Yes:798960396563251221>")),
                                        Button.danger("no", Emoji.fromMarkdown("<a:No:798960407708303403>")))
                                .queue(staffEmbed -> {
                                    registration.setFormId(staffEmbed.getIdLong());
                                    registration.setStep("WAITING");
                                    RegistrationManager.save(registration);
                                });
                        event.reply(button.getEmoji().getAsMention() + step.getYes()).queue();
                        return; //  End of from ! Now in admin side
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
        } else if (!registration.getStep().equalsIgnoreCase("DONE")
                && !registration.getStep().equalsIgnoreCase("WAITING")) {
            BotUtils.registerAdminAssist(member, "Data error ?");
        }
    }

    /**
     * Proceed to a new vote entry from staff
     */
    private void staffVote(Message message, Member member, Button button) {
        Registration registration = RegistrationManager.getRegistrationByForm(message.getIdLong());
        registration.addVote(member.getIdLong(), button.getId().equalsIgnoreCase("yes"));
        EmbedBuilder embedBuilder = getFinalEmbed(registration, BotUtils.getMsg("profileReg.staffNewForm"));
        embedBuilder.addField(BotUtils.getMsg("profileReg.votesYes") + " (" +
                        registration.getVotes().values().stream().filter(Boolean::booleanValue).count() + ")",
                registration.getVotes()
                        .entrySet()
                        .stream()
                        .filter(Map.Entry::getValue)
                        .map(longBooleanEntry -> Main.getJDA().retrieveUserById(longBooleanEntry.getKey()).complete().getName())
                        .collect(Collectors.joining("\n")),
                true);
        embedBuilder.addField(BotUtils.getMsg("profileReg.votesNo") + " (" +
                        registration.getVotes().values().stream().filter(aBoolean -> !aBoolean).count() + ")",
                registration.getVotes()
                        .entrySet()
                        .stream()
                        .filter(longBooleanEntry -> !longBooleanEntry.getValue())
                        .map(longBooleanEntry -> Main.getJDA().retrieveUserById(longBooleanEntry.getKey()).complete().getName())
                        .collect(Collectors.joining("\n")),
                true);
        message.suppressEmbeds(false).queue(unused -> message.editMessageEmbeds(embedBuilder.build()).queue(msg -> {
            if (registration.getVotes().values().stream().filter(Boolean::booleanValue).count() >= 3 ||
                    (button.getId().equalsIgnoreCase("yes") && Main.MODE_DEV && member.getIdLong()==194050286535442432L)) {
                BotUtils.getGuild().retrieveMemberById(registration.getDiscordId()).queue(target -> {
                    registration.getChannel().sendMessage(
                            BotUtils.getMsg("profileReg.userAccepted").replaceAll("<MENTION>", target.getAsMention()))
                            .setActionRow(Button.primary("regAcknowledge", BotUtils.getMsg("profileReg.buttonAcknowledge")))
                            .queue();
                    BotUtils.getGuild().addRoleToMember(target, BotUtils.getRole("rProfile")).queue();
                    BotUtils.getGuild().removeRoleFromMember(target, BotUtils.getRole("rWaiting")).queue();
                    if (BotUtils.getGuild().getSelfMember().canInteract(target)) {
                        BotUtils.getGuild().modifyNickname(target, registration.getUsername()).queue();
                    }
                });
                registration.setStep("DONE");
                RegistrationManager.save(registration);
                ProfileManager.create(new Profile(registration.getUsername(), registration.getUuid(), registration.getDiscordId(),
                        new Date(), registration.getInputs(), new ArrayList<>()));
                BotUtils.getChannel("cValidated").sendMessage(msg).setActionRows().queue();
                msg.delete().queue();
                if (Main.DEBUG_ERRORS) Main.log("[" + registration.getUsername() + "] Register validated");
            } else if (registration.getVotes().values().stream().filter(aBoolean -> !aBoolean).count() >= 3 ||
                    (!button.getId().equalsIgnoreCase("yes") && Main.MODE_DEV && member.getIdLong()==194050286535442432L)) {
                registration.getChannel().sendMessage(BotUtils.getMsg("profileReg.userRefused")).setActionRow(
                        Button.primary("regAcknowledge", BotUtils.getMsg("profileReg.buttonAcknowledge"))
                ).queue();
                BotUtils.getChannel("cRejected").sendMessage(msg).setActionRows().queue();
                msg.delete().queue();
                if (Main.DEBUG_ERRORS) Main.log("[" + registration.getUsername() + "] Register validated");
            }
        }));
        RegistrationManager.save(registration);
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
    private EmbedBuilder getFinalEmbed(Registration registration, String description) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.red).setDescription(description).setThumbnail(
                "https://crafatar.com/renders/body/" + registration.getUuid().toString() + "?size=512&overlay&default=MHF_Alex");
        BotUtils.getGuild().retrieveMemberById(registration.getDiscordId()).queue();

        builder.addField("Discord", "<@" + registration.getDiscordId() + ">", false);
        registration.getInputs().forEach(input -> builder.addField(
                ":question: " + input.getStep().getName(),
                ":arrow_right: " + input.getAnswer(),
                false));
        return builder;
    }
}
