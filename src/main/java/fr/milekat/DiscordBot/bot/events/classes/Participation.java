package fr.milekat.DiscordBot.bot.events.classes;

import java.util.UUID;

public class Participation {
    private final UUID uuid;
    private final String eventName;

    public Participation(UUID uuid, String eventName) {
        this.uuid = uuid;
        this.eventName = eventName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getEventName() {
        return eventName;
    }
}
