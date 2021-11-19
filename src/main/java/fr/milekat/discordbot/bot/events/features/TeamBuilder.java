package fr.milekat.discordbot.bot.events.features;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.events.classes.Event;
import fr.milekat.discordbot.bot.events.classes.Team;
import fr.milekat.discordbot.bot.events.managers.EventManager;
import fr.milekat.discordbot.bot.events.managers.TeamManager;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import fr.milekat.discordbot.bot.master.core.managers.ProfileManager;
import fr.milekat.utils.DateMileKat;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class TeamBuilder extends ListenerAdapter {
    private final HashMap<Long, Date> requestCD = new HashMap<>();

    public TeamBuilder() {
        BotUtils.getGuild().upsertCommand(BotUtils.getCommandWithSub("teamBuilder.command").setDefaultEnabled(false)
        ).queue(command -> BotUtils.getGuild().updateCommandPrivilegesById(command.getIdLong(),
                new CommandPrivilege(CommandPrivilege.Type.ROLE, true, BotUtils.getRole("rProfile").getIdLong())
        ).queue());
    }

    /**
     * Slash command "team" (Create, rename, open/close)
     */
    @Override
    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        if (!event.getName().equalsIgnoreCase("team") || event.getTextChannel().getParent()==null) return;
        //  Check if command is execute in "teamChannelName" (=team-builder channel)
        if (event.getTextChannel().getName().equalsIgnoreCase(BotUtils.getMsg("teamBuilder.teamChannelName"))) {
            //  If yes, you can only create a team in team-builder channel
            if (!event.getSubcommandName().equalsIgnoreCase("creer")) {
                BotUtils.reply(event, "teamBuilder.wrongChannel");
                return;
            }
            //  Get Event and user Profile
            Event mcEvent = EventManager.getEventCtMain(event.getTextChannel().getParent().getIdLong());
            Profile profile = ProfileManager.getProfile(event.getUser().getIdLong());
            //  Check if this team exists in this event
            if (TeamManager.exists(mcEvent, event.getOption("nom_equipe").getAsString())) {
                BotUtils.reply(event, "teamBuilder.teamCreateError");
                return;
            }
            //  Check if member has already a team on this event
            if (TeamManager.exists(mcEvent, profile)) {
                BotUtils.reply(event, "teamBuilder.alreadyTeaming");
                return;
            }
            //  Create the new team
            Team team = new Team(mcEvent.getName(), event.getOption("nom_equipe").getAsString(), profile);
            Main.log("[Event] New team '%s' in event '%s' created by '%s'."
                    .formatted(team.getTeamName(), mcEvent.getName(), event.getUser().getAsTag()));
            BotUtils.reply(event, "teamBuilder.teamCreated", Collections.singletonMap("<name>", team.getTeamName()));
            TeamManager.create(team);
            //  Create team private channel and add owner into it
            mcEvent.getCategoryTeam().createTextChannel(team.getTeamName()).queue(textChannel -> {
                team.setChannelId(textChannel.getIdLong());
                TeamManager.updateChannelId(team);
                textChannel.putPermissionOverride(Objects.requireNonNull(event.getMember()))
                        .setAllow(Permission.VIEW_CHANNEL).queue();
            });
            //  Send presentation message
            event.getTextChannel().sendMessage(team.getTeamName()).queue(message -> {
                team.setMessageId(message.getIdLong());
                TeamManager.updateMessageId(team);
                updatePresentation(team);
            });
        } else {
            //  else, you are in team category (Normally)
            if (event.getSubcommandName().equalsIgnoreCase("creer")) {
                BotUtils.reply(event, "teamBuilder.wrongChannel");
                return;
            }
            //  Get Event and then team from category if exists, if not send wrongChannel
            Event mcEvent = EventManager.getEventCtTeam(event.getTextChannel().getParent().getIdLong());
            if (mcEvent==null) {
                BotUtils.reply(event, "teamBuilder.wrongChannel");
                return;
            }
            Team team = TeamManager.getTeam(mcEvent, event.getTextChannel().getIdLong());
            if (team==null) {
                BotUtils.reply(event, "teamBuilder.wrongChannel");
                return;
            }
            //  Profile of user (If user is an admin use profile of the team owner
            Profile profile = allowProfile(event, team);
            if (profile==null) return;
            if (event.getSubcommandName().equalsIgnoreCase("renommer")) {
                if (TeamManager.exists(mcEvent, event.getOption("nom").getAsString())) {
                    BotUtils.reply(event, "teamBuilder.teamRenameError");
                    return;
                }
                team.setTeamName(event.getOption("nom").getAsString());
                BotUtils.reply(event, "teamBuilder.teamRename", Collections.singletonMap("<name>", team.getTeamName()));
                Main.log("[" + event.getUser().getAsTag() + "] rename team with " + team.getTeamName() + ".");
                team.getChannel().getManager().setName(team.getTeamName()).queue();
                TeamManager.updateName(team);
            } else if (event.getSubcommandName().equalsIgnoreCase("access")) {
                team.setAccess(event.getOption("access").getAsBoolean());
                BotUtils.reply(event, "teamBuilder.teamAccess",
                        Collections.singletonMap("<access>", String.valueOf(team.isOpen())));
                Main.log("[" + event.getUser().getAsTag() + "] set access to " + team.isOpen() + ".");
                TeamManager.updateAccess(team);
            } else if (event.getSubcommandName().equalsIgnoreCase("description")) {
                team.setDescription(event.getOption("description").getAsString());
                BotUtils.reply(event, "teamBuilder.teamDescription",
                        Collections.singletonMap("<description>", team.getDescription()));
                Main.log("[" + event.getUser().getAsTag() + "] update description: " + team.getDescription() + ".");
                TeamManager.updateDescription(team);
            }
            updatePresentation(team);
        }
    }

    /**
     * 1:   User click on a request to join an opened team
     * 2:   Owner accept or decline the request
     */
    @Override
    public void onButtonClick(@Nonnull ButtonClickEvent event) {
        if (event.getTextChannel().getParent()==null) return;
        if (event.getTextChannel().getName().equalsIgnoreCase(BotUtils.getMsg("teamBuilder.teamChannelName"))) {
            if (requestCD.containsKey(event.getUser().getIdLong())){
                Date endCd = new Date(requestCD.get(event.getUser().getIdLong()).getTime() + 300000);
                if (new Date().before(endCd)) {
                    event.reply("Please don't spam, wait " + DateMileKat.reamingToString(endCd))
                            .setEphemeral(true).queue();
                    return;
                }
            }
            requestCD.remove(event.getUser().getIdLong());
            Event mcEvent = EventManager.getEventCtMain(event.getTextChannel().getParent().getIdLong());
            Team team = TeamManager.getTeamByMsg(mcEvent, event.getMessage().getIdLong());
            if (team==null) {
                event.reply("Error with this team").setEphemeral(true).queue();
                return;
            }
            team.getChannel().sendMessage(BotUtils.getMsg("teamBuilder.playerRequest")
                            .replaceAll("<MENTION>", event.getUser().getAsMention()))
                    .setActionRow(Button.primary("yes", "Accept"), Button.danger("no", "Decline")).queue();
            BotUtils.reply(event, "teamBuilder.requestSent");
            requestCD.put(event.getUser().getIdLong(), new Date());
        } else {
            Event mcEvent = EventManager.getEventCtTeam(event.getTextChannel().getParent().getIdLong());
            if (mcEvent==null) return;
            Team team = TeamManager.getTeam(mcEvent, event.getTextChannel().getIdLong());
            if (team==null) return;
            //  Profile of user (If user is an admin use profile of the team owner
            Profile profile = allowProfile(event, team);
            if (profile==null) return;
            if (event.getButton().getId().equalsIgnoreCase("yes")) {
                Profile joiner = ProfileManager.getProfile(event.getMessage().getMentionedUsers().get(0).getIdLong());
                if (team.getSize() <= mcEvent.getTeamSize() && !team.getMembers().contains(joiner.getUuid())) {
                    team.addMember(joiner);
                    TeamManager.updateMembers(team);
                    BotUtils.getGuild().retrieveMemberById(event.getMessage().getMentionedUsers().get(0).getIdLong())
                            .queue(member -> event.getTextChannel().putPermissionOverride(member)
                                    .setAllow(Permission.VIEW_CHANNEL).queue(ignored ->
                                            event.getTextChannel().sendMessage(BotUtils.getMsg("teamBuilder.joinNotify")
                                                    .replaceAll("<MENTION>", member.getAsMention())).queue()));
                } else {
                    event.reply("Team full or player already in team.").setEphemeral(true).queue();
                }
                updatePresentation(team);
                event.getMessage().delete().queue();
            } else if (event.getButton().getId().equalsIgnoreCase("no")) {
                event.getMessage().delete().queue();
            }
        }
    }

    /**
     * Update every display, members, etc... of team
     */
    private void updatePresentation(Team team) {
        Optional<TextChannel> channel = team.getEvent().getCategory().getTextChannels().stream()
                .filter(textCh -> textCh.getName().equalsIgnoreCase(BotUtils.getMsg("teamBuilder.teamChannelName")))
                .findFirst();
        channel.ifPresent(textChannel -> textChannel.retrieveMessageById(team.getMessageId()).queue(message -> {
            MessageEmbed embed = new EmbedBuilder()
                    .setAuthor(team.getTeamName())
                    .setDescription(team.getDescription())
                    .addField("Members", team.getMembersProfiles().stream().map(profile -> profile.getUsername() +
                            System.lineSeparator()).collect(Collectors.joining()), true)
                    .addField("Access", team.isOpen() ? "Open" : "Close", true)
                    .setFooter("Chef -> " + team.getOwnerProfile().getUsername())
                    .build();
            Component button = Button.primary(team.getTeamName(), "Request to join");
            if (!team.isOpen()) button = Button.secondary(team.getTeamName(), "Team closed").asDisabled();
            // TODO: 05/08/2021 [JDA5 needed] Open a thread on this message if not open
            message.editMessage("Team -> " + team.getTeamName()).queue();
            message.editMessageEmbeds(embed).setActionRow(button).queue();
            }, err -> Main.log("[Error] Can't find message from team " + team.getTeamName() + " error: " + err.getMessage())
        ));
    }

    @Nullable
    private Profile allowProfile(GenericInteractionCreateEvent event, Team team) {
        Profile profile;
        if (event.getMember().getRoles().contains(BotUtils.getRole("rAdmin"))) {
            profile = team.getOwnerProfile();
        } else {
            profile = ProfileManager.getProfile(event.getUser().getIdLong());
        }
        //  Check if user is the owner (Or an Admin working as the owner)
        if (profile!=null && !team.getOwner().equals(profile.getUuid())) {
            BotUtils.reply(event, "noPerm");
            profile = null;
        }
        return profile;
    }
}
