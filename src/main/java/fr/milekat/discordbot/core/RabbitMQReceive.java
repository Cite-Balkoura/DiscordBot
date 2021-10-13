package fr.milekat.discordbot.core;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.events.classes.Event;
import fr.milekat.discordbot.bot.events.classes.Team;
import fr.milekat.discordbot.bot.events.managers.EventManager;
import fr.milekat.discordbot.bot.events.managers.TeamManager;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import fr.milekat.discordbot.bot.master.core.managers.ProfileManager;
import fr.milekat.discordbot.bot.master.moderation.ModerationUtils;
import net.dv8tion.jda.api.entities.Category;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import java.util.UUID;

public class RabbitMQReceive {
    private final MessageType type;
    private final JSONObject payload;

    public RabbitMQReceive (MessageType type, JSONObject payload) {
        this.type = type;
        this.payload = payload;
        process();
    }

    public enum MessageType {
        mute,
        unMute,
        ban,
        unBan,
        chatGlobal,
        chatTeam,
        other
    }

    private void process() {
        switch (type) {
            case ban, unBan, mute, unMute -> {
                Profile target = ProfileManager.getProfile(UUID.fromString((String) payload.get("target")));
                Profile sender = ProfileManager.getProfile(UUID.fromString((String) payload.get("sender")));
                Long delay = (Long) payload.get("delay");
                String reason = (String) payload.get("reason");
                BotUtils.getGuild().retrieveMemberById(target.getDiscordId()).queue(member -> {
                    switch (type) {
                        case ban -> ModerationUtils.ban(member, target, sender, delay, reason);
                        case unBan -> ModerationUtils.unBan(member, target, reason);
                        case mute -> ModerationUtils.mute(member, target, sender, delay, reason);
                        case unMute -> ModerationUtils.unMute(member, target, reason);
                    }
                });
            }
            case chatGlobal -> {
                Event mcEvent = EventManager.getEvent((String) payload.get("event"));
                Category category = BotUtils.getGuild().getCategoryById(mcEvent.getCategoryId());
                if (category==null) {
                    Main.log("[Error] Event or Category of event " + payload.get("event") + " not found.");
                    return;
                }
                category.getTextChannels().stream()
                        .filter(textChannel -> textChannel.getName().equalsIgnoreCase("chat"))
                        .forEach(textChannel -> textChannel.sendMessage((String) payload.get("message")).queue());
            }
            case chatTeam -> {
                Event mcEvent = EventManager.getEvent((String) payload.get("event"));
                Team team = TeamManager.getTeam(mcEvent, new ObjectId((String) payload.get("teamId")));
                team.getChannel().sendMessage((String) payload.get("message")).queue();
            }
        }
    }
}
