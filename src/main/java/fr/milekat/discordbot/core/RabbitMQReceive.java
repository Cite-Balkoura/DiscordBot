package fr.milekat.discordbot.core;

import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.Moderation.ModerationUtils;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import fr.milekat.discordbot.bot.master.core.managers.ProfileManager;
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
        unmute,
        ban,
        unban,
        other
    }

    private void process() {
        switch (type) {
            case ban, unban, mute, unmute -> {
                Profile target = ProfileManager.getProfile(UUID.fromString((String) payload.get("target")));
                Profile sender = ProfileManager.getProfile(UUID.fromString((String) payload.get("sender")));
                Long delay = (Long) payload.get("delay");
                String reason = (String) payload.get("reason");
                BotUtils.getGuild().retrieveMemberById(target.getDiscordId()).queue(member -> {
                    switch (type) {
                        case ban -> ModerationUtils.ban(member, target, sender, delay, reason);
                        case unban -> ModerationUtils.unBan(member, target, reason);
                        case mute -> ModerationUtils.mute(member, target, sender, delay, reason);
                        case unmute -> ModerationUtils.unMute(member, target, reason);
                    }
                });
            }
        }
    }
}
