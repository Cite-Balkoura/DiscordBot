package fr.milekat.discordbot.bot.events.features;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.events.classes.Event;
import fr.milekat.discordbot.bot.events.classes.Team;
import fr.milekat.discordbot.bot.events.managers.EventManager;
import fr.milekat.discordbot.bot.events.managers.TeamManager;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import fr.milekat.discordbot.bot.master.core.managers.ProfileManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Objects;

public class TeamBuilder extends ListenerAdapter {
    public TeamBuilder() {
        BotUtils.getGuild().upsertCommand(new CommandData("team", BotUtils.getMsg("teamBuilder.slashCreateTeamDesc"))
                .addSubcommands(new SubcommandData("create",
                        BotUtils.getMsg("teamBuilder.slashOptDescName"))
                        .addOption(OptionType.STRING,
                                "name",
                                BotUtils.getMsg("teamBuilder.slashOptDescName"),
                                true
                        )
                ).addSubcommands(new SubcommandData("rename",
                        BotUtils.getMsg("teamBuilder.slashOptDescRename"))
                        .addOption(OptionType.STRING,
                                "name",
                                BotUtils.getMsg("teamBuilder.slashOptDescRename"),
                                true
                        )
                ).addSubcommands(new SubcommandData("open",
                        BotUtils.getMsg("teamBuilder.slashOptDescOpen"))
                        .addOption(OptionType.BOOLEAN,
                                "open",
                                BotUtils.getMsg("teamBuilder.slashOptDescOpen"),
                                true
                        )
                ).setDefaultEnabled(false)
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
            if (event.getSubcommandName().equalsIgnoreCase("create")) {
                //  Get Event and user Profile
                Event mcEvent = EventManager.getEvent(event.getTextChannel().getParent().getIdLong());
                Profile profile = ProfileManager.getProfile(event.getUser().getIdLong());
                //  Check if this team exists in this event
                if (TeamManager.exists(mcEvent, event.getOption("name").getAsString())) {
                    BotUtils.reply(event, "teamBuilder.teamCreateError");
                    return;
                }
                //  Check if member has already a team on this event
                if (TeamManager.exists(mcEvent, profile)) {
                    BotUtils.reply(event, "teamBuilder.alreadyTeaming");
                    return;
                }
                Team team = new Team(mcEvent.getName(), event.getOption("name").getAsString(), profile);
                Main.log("[Event] New team '%s' in event '%s' created by '%s'.".formatted(team.getTeamName(), mcEvent.getName(),
                        event.getUser().getAsTag()));
                BotUtils.reply(event, "teamBuilder.teamCreated", Collections.singletonMap("<name>", team.getTeamName()));
                team.addMember(profile);
                TeamManager.save(team);
                event.getTextChannel().sendMessage("loading team...").queue(message -> {
                    team.setMessageId(message.getIdLong());
                    TeamManager.updateMessageId(team);
                    updatePresentation(team);
                });
                mcEvent.getCategoryTeam().createTextChannel(team.getTeamName()).queue(textChannel ->
                        textChannel.putPermissionOverride(Objects.requireNonNull(event.getMember()))
                                .setAllow(Permission.VIEW_CHANNEL).queue());
            } else {
                event.reply(BotUtils.getMsg("teamBuilder.wrongChannel")).setEphemeral(true).queue();
            }
        } else {
            //  else, you are in team category (Normally)
            //  Get Event and then team from category if exists, if not send wrongChannel
            Event mcEvent = EventManager.getEventTeam(event.getTextChannel().getParent().getIdLong());
            if (mcEvent==null) {
                event.reply(BotUtils.getMsg("teamBuilder.wrongChannel")).setEphemeral(true).queue();
                return;
            }
            Team team = TeamManager.getTeam(mcEvent, event.getTextChannel().getIdLong());
            if (team==null) {
                event.reply(BotUtils.getMsg("teamBuilder.wrongChannel")).setEphemeral(true).queue();
                return;
            }
            //  Profile of user (If user is an admin use profile of the team owner
            Profile profile;
            if (event.getMember().getRoles().contains(BotUtils.getRole("rAdmin"))) {
                profile = team.getOwner();
            } else {
                profile = ProfileManager.getProfile(event.getUser().getIdLong());
            }
            //  Check if user is the owner (Or an Admin working as the owner)
            if (!team.getOwner().getUuid().equals(profile.getUuid())) {
                BotUtils.reply(event, "noPerm");
                return;
            }
            if (event.getSubcommandName().equalsIgnoreCase("rename")) {
                team.setTeamName(event.getOption("name").getAsString());
                BotUtils.reply(event, "teamBuilder.teamRename", Collections.singletonMap("<name>", team.getTeamName()));
                Main.log("[" + event.getMember().getUser().getAsTag() + "] rename team with " + team.getTeamName() + ".");
                team.getChannel().getManager().setName(team.getTeamName()).queue();
                TeamManager.updateName(team);
            } else if (event.getSubcommandName().equalsIgnoreCase("open")) {
                team.setOpen(event.getOption("open").getAsBoolean());
                BotUtils.reply(event, "teamBuilder.teamOpen",
                        Collections.singletonMap("<open>", String.valueOf(team.isOpen())));
                Main.log("[" + event.getMember().getUser().getAsTag() + "] set open to " + team.isOpen() + ".");
                TeamManager.updateOpen(team);
            }
        }
    }

    /**
     * User click on a team invitation
     */
    @Override
    public void onButtonClick(@Nonnull ButtonClickEvent event) {
        // TODO: 25/07/2021 Check channel
        // TODO: 25/07/2021 Send acknowledge to user (if good or not)
    }

    /**
     * Update every display, members, etc... of team
     */
    private void updatePresentation(Team team) {
        // TODO: 08/10/2021 ava.lang.NullPointerException: Cannot invoke "net.dv8tion.jda.api.entities.TextChannel.retrieveMessageById(long)" because the return value of "net.dv8tion.jda.api.JDA.getTextChannelById(long)" is null
        Main.getJDA().getTextChannelById(team.getEvent().getCategoryId()).retrieveMessageById(team.getMessageId()).queue(message -> {
            /*
            event.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setAuthor(team.getName())
                        .setDescription()
                        .setFooter(profile.getUsername(), event.getMember().getUser().getAvatarUrl())
                        .build()
                ).setActionRow(
                        // TODO: 05/08/2021 Update team button
                ).queue();*/
            //message.editMessageEmbeds().queue();
            // TODO: 05/08/2021 Open a thread on this message if not open
            message.editMessage(team.getTeamName()).queue();
            // TODO: 08/10/2021 Do something better, it's the presentation message
        });
    }
}
