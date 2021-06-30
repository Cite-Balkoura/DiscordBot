package fr.milekat.discordbot.bot.events.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Reference;

import java.util.UUID;

@Entity(value = "participation")
public class Participation {
    @Indexed
    private final UUID uuid;
    @Reference(idOnly = true)
    private final Event event;

    public Participation(UUID uuid, Event event) {
        this.uuid = uuid;
        this.event = event;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Event getEvent() {
        return event;
    }
}
