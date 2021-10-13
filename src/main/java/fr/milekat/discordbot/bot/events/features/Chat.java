package fr.milekat.discordbot.bot.events.features;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.events.classes.Event;
import fr.milekat.discordbot.bot.events.classes.Team;
import fr.milekat.discordbot.bot.events.managers.EventManager;
import fr.milekat.discordbot.bot.events.managers.TeamManager;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import fr.milekat.discordbot.bot.master.core.managers.ProfileManager;
import fr.milekat.discordbot.core.RabbitMQ;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.types.ObjectId;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Chat extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getChannel().getParent()==null) return;
        EventManager.getEvents().forEach(mcEvent -> {
            if (event.getChannel().getParent().getIdLong()==mcEvent.getCategoryId()) {
                if (event.getChannel().getName().equalsIgnoreCase("chat")) {
                    Profile profile = ProfileManager.getProfile(event.getAuthor().getIdLong());
                    rabbitSend("chatGlobal", mcEvent, profile.getUuid(), new ObjectId(), event.getMessage().getContentRaw());
                    event.getMessage().delete().queue();
                } else if (event.getChannel().getName().equalsIgnoreCase(BotUtils.getMsg("teamBuilder.teamChannelName"))) {
                    event.getMessage().delete().queue();
                }
            } else if (event.getChannel().getParent().getIdLong()== mcEvent.getCategoryTeamId()) {
                Profile profile = ProfileManager.getProfile(event.getAuthor().getIdLong());
                Team team = TeamManager.getTeam(mcEvent, event.getChannel().getIdLong());
                rabbitSend("chatTeam", mcEvent, profile.getUuid(), team.getId(), event.getMessage().getContentRaw());
                event.getMessage().delete().queue();
            }
        });
    }

    /**
     * Send a chatGlobal or a chatTeam message through RabbitMq
     */
    private void rabbitSend(String type, Event mcEvent, UUID uuid, ObjectId teamId, String message) {
        try {
            RabbitMQ.rabbitSend(mcEvent.getDatabase(),
                    String.format("{\"type\":\"%s\",\"event\":\"%s\",\"uuid\":\"%s\",\"teamId\":\"%s\",\"message\": \"%s\"}",
                    type, mcEvent.getName(), uuid.toString(), teamId.toString(), message));
        } catch (IOException | TimeoutException exception) {
            Main.log("[Error] RabbitSend - " + type);
            if (Main.DEBUG_ERRORS) exception.printStackTrace();
        }
    }
}
