package fr.milekat.discordbot.bot.events.features;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotManager;
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
                                .equalsIgnoreCase(BotManager.getMsg("teamBuilder.teamChannelName")))
                        .findFirst().ifPresent(TEAM_CHANNELS_LISTENER::add));
        //  Update Slash Command /team
        BotManager.getGuild().upsertCommand(
                new CommandData("team", BotManager.getMsg("teamBuilder.slashCreateTeamDesc"))
                        .addOption(OptionType.STRING,
                                "name",
                                BotManager.getMsg("teamBuilder.slashOptDescName"),
                                true))
                .queue();
    }

    /**
     * Slash command "Team create"
     */
    @Override
    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        if (!event.getName().equalsIgnoreCase("team")) return;
        //  Check if channel is a TEAM_CHANNELS_LISTENER
        if (event.getChannel().getType()!= ChannelType.TEXT || !TEAM_CHANNELS_LISTENER.contains(event.getTextChannel())) {
            //event.reply(BotManager.getMsg()).setEphemeral(true).queue();
            BotManager.reply(event, "teamBuilder.wrongChannel");
            return;
        }
        //  Get Event and user Profile
        Event mcEvent = EventManager.getEvent(event.getTextChannel().getParent().getIdLong());
        Profile profile = ProfileManager.getProfile(event.getUser().getIdLong());
        //  Check if this team exists in this event
        if (TeamManager.exists(mcEvent, event.getOption("name").getAsString())) {
            BotManager.reply(event, "teamBuilder.teamCreateError");
            return;
        }
        Team team = new Team(mcEvent, event.getOption("name").getAsString());
        Main.log("[Event] New team '%s' in event '%s' created by '%s'.".formatted(team.getName(), mcEvent.getName(), event.getUser().getAsTag()));
        BotManager.reply(event, "teamBuilder.teamCreated", Collections.singletonMap("<name>", team.getName()));
        // TODO: 28/07/2021 Add the profile of chief in team, to prevent duplicate null members in team
        //team.getMembers().add(profile);
        TeamManager.save(team);
    }

    /**
     * User click on an team invitation
     */
    @Override
    public void onButtonClick(@Nonnull ButtonClickEvent event) {
        // TODO: 25/07/2021 Check channel
        // TODO: 25/07/2021 Send acknowledge to user (if good or not)
    }
}
