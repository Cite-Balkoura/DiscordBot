package fr.milekat.discordbot.bot.events.classes;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.discordbot.Main;

import java.util.ArrayList;
import java.util.UUID;

public class ParticipationManager {
    private static final Datastore DATASTORE = Main.getDatastore("master");

    /**
     * Get a list of participation for a player by his uuid
     */
    public static ArrayList<Participation> getParticipationList(UUID uuid) {
        return new ArrayList<>(DATASTORE.find(Participation.class)
                .filter(Filters.all("uuid", uuid))
                .iterator().toList());
    }

    /**
     * Get a participation from a player by his uuid and the event
     */
    public static Participation getParticipation(UUID uuid, Event event) {
        return DATASTORE.find(Participation.class)
                .filter(Filters.and(Filters.eq("uuid", uuid), Filters.eq("event", event)))
                .first();
    }

    /**
     * Save a new participation (Can't modify an existing one)
     */
    public static void save(Participation participation) {
        DATASTORE.save(participation);
    }
}
