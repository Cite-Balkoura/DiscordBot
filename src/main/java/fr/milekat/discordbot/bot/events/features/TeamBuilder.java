package fr.milekat.discordbot.bot.events.features;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotManager;
import fr.milekat.discordbot.bot.events.classes.Event;
import fr.milekat.discordbot.bot.events.managers.EventManager;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class TeamBuilder extends ListenerAdapter {
    private final ArrayList<TextChannel> TEAM_CHANNELS = new ArrayList<>();
    public TeamBuilder() {
        //  Add every "team-channel" from every events into TEAM_CHANNELS ArrayList
        EventManager.getEvents().stream()
                .filter(event -> event.getEventFeatures().stream().anyMatch(
                        eventFeature -> eventFeature.getName().equalsIgnoreCase("team")))
                .map(Event::getCategoryId).forEach(id -> Main.getJDA().getCategoryById(id).getTextChannels().stream()
                .filter(textChannel -> textChannel.getName().equalsIgnoreCase(BotManager.getMsg("teamBuilder.teamChannelName")))
                .findFirst().ifPresent(TEAM_CHANNELS::add));
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
        if (event.getChannel().getType()!= ChannelType.TEXT || !TEAM_CHANNELS.contains(event.getTextChannel())) {
            event.reply(BotManager.getMsg("teamBuilder.wrongChannel")).setEphemeral(true).queue();
            return;
        }
        event.reply(BotManager.getMsg("teamBuilder.teamCreated")
                .replaceAll("<name>", event.getOption("name").getAsString())).setEphemeral(true).queue();
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
