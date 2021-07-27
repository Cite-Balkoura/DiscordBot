package fr.milekat.discordbot.bot.events.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Reference;
import org.bson.types.ObjectId;

import java.util.UUID;

@Entity(value = "participation")
public class Participation {
    @Id
    private ObjectId id;
    @Indexed
    private UUID uuid;
    @Reference(idOnly = true)
    private Event event;

    public Participation() {}

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
