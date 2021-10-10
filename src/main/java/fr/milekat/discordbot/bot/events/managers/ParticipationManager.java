package fr.milekat.discordbot.bot.events.managers;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.events.classes.Participation;
import fr.milekat.discordbot.bot.master.core.classes.Profile;

import java.util.ArrayList;

public class ParticipationManager {
    private static final Datastore DATASTORE = Main.getDatastore("master");

    /**
     * Get a list of participation for a player by his uuid
     */
    public static ArrayList<Participation> getParticipationList(Profile profile) {
        return new ArrayList<>(DATASTORE.find(Participation.class)
                .filter(Filters.eq("profile", profile))
                .iterator().toList());
    }

    /**
     * Create a new participation (Can't modify an existing one)
     */
    public static void create(Participation participation) {
        DATASTORE.save(participation);
    }
}
