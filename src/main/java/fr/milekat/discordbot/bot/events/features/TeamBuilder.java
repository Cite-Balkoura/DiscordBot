package fr.milekat.discordbot.bot.events.features;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.events.classes.Event;
import fr.milekat.discordbot.bot.events.classes.Team;
import fr.milekat.discordbot.bot.events.managers.EventManager;
import fr.milekat.discordbot.bot.events.managers.TeamManager;
import fr.milekat.discordbot.bot.master.classes.Profile;
import fr.milekat.discordbot.bot.master.managers.ProfileManager;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;

public class TeamBuilder extends ListenerAdapter {
    private final ArrayList<TextChannel> TEAM_CHANNELS_LISTENER = new ArrayList<>();
    public TeamBuilder() {
        //  Set listeners channels (Channels to listen)
        EventManager.getEvents().stream()
                .filter(event -> event.getEventFeatures().stream().anyMatch(eventFeature -> eventFeature.equals(Event.EventFeature.TEAM)))
                .forEach(event -> Main.getJDA().getCategoryById(event.getCategoryId()).getTextChannels().stream()
                        .filter(textChannel -> textChannel.getName()
                                .equalsIgnoreCase(BotUtils.getMsg("teamBuilder.teamChannelName")))
                        .findFirst().ifPresent(TEAM_CHANNELS_LISTENER::add));
        //  Update Slash Command /team
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
     * Slash command "Team create"
     */
    @Override
    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        if (!event.getName().equalsIgnoreCase("team")) return;
        //  Check if channel is a TEAM_CHANNELS_LISTENER
        if (event.getChannel().getType()!= ChannelType.TEXT || !TEAM_CHANNELS_LISTENER.contains(event.getTextChannel())) {
            BotUtils.reply(event, "teamBuilder.wrongChannel");
            return;
        }
        //  Check if member has role Profile
        if (!event.getMember().getRoles().contains(BotUtils.getRole("rProfile"))) {
            BotUtils.reply(event, "noPerm");
            return;
        }
        //  Get Event and user Profile
        Event mcEvent = EventManager.getEvent(event.getTextChannel().getParent().getIdLong());
        Profile profile = ProfileManager.getProfile(event.getUser().getIdLong());
        if (event.getSubcommandName().equalsIgnoreCase("create")) {
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
            Team team = new Team(mcEvent, event.getOption("name").getAsString(), profile);
            Main.log("[Event] New team '%s' in event '%s' created by '%s'.".formatted(team.getName(), mcEvent.getName(), event.getUser().getAsTag()));
            BotUtils.reply(event, "teamBuilder.teamCreated", Collections.singletonMap("<name>", team.getName()));
            team.addMember(profile);
            TeamManager.save(team);
            event.getTextChannel().sendMessage("loading team...").queue(message -> {
                team.setMessageId(message.getIdLong());
                TeamManager.save(team);
                updateTeam(team);
            });
        } else if (event.getSubcommandName().equalsIgnoreCase("rename")) {
            //  Check if this team exists in this event
            if (TeamManager.exists(mcEvent, event.getOption("name").getAsString())) {
                BotUtils.reply(event, "teamBuilder.teamCreateError");
                return;
            }
            Team team = TeamManager.getTeam(mcEvent, profile);
            if (!(team.getOwner().getDiscordId()==profile.getDiscordId())) {
                BotUtils.reply(event, "noPerm");
                return;
            }
            team.setName(event.getOption("name").getAsString());
            BotUtils.reply(event, "teamBuilder.teamRename", Collections.singletonMap("<name>", team.getName()));
            Main.log("[" + event.getMember().getUser().getAsTag() + "] rename team with " + team.getName() + ".");
            TeamManager.save(team);
        } else if (event.getSubcommandName().equalsIgnoreCase("open")) {
            Team team = TeamManager.getTeam(mcEvent, profile);
            if (!(team.getOwner().getDiscordId()==profile.getDiscordId())) {
                BotUtils.reply(event, "noPerm");
                return;
            }
            team.setOpen(event.getOption("open").getAsBoolean());
            BotUtils.reply(event, "teamBuilder.teamOpen", Collections.singletonMap("<open>", String.valueOf(team.isOpen())));
            Main.log("[" + event.getMember().getUser().getAsTag() + "] set open to " + team.isOpen() + ".");
            TeamManager.save(team);
        }
    }

    /**
     * User click on an team invitation
     */
    @Override
    public void onButtonClick(@Nonnull ButtonClickEvent event) {
        // TODO: 25/07/2021 Check channel
        // TODO: 25/07/2021 Send acknowledge to user (if good or not)
    }

    /**
     * Update every display, members, etc... of team
     */
    private void updateTeam(Team team) {
        Main.getJDA().getTextChannelById(team.getChannelId()).retrieveMessageById(team.getMessageId()).queue(message -> {
            /*
            event.getTextChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setAuthor(team.getName())
                        .setDescription()
                        .setFooter(profile.getUsername(), event.getMember().getUser().getAvatarUrl())
                        .build()
                ).setActionRow(
                        // TODO: 05/08/2021 Update team button
                ).queue();*/
            message.editMessageEmbeds().queue();
            // TODO: 05/08/2021 Open a thread on this message if not open
        });
    }
}
