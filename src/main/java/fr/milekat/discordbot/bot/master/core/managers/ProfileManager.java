package fr.milekat.discordbot.bot.master.core.managers;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.core.classes.Profile;

import java.util.UUID;

public class ProfileManager {
    private static final Datastore DATASTORE = Main.getDatastore("master");

    /**
     * Get a Profile by his discord id
     */
    public static Profile getProfile(Long id) {
        return DATASTORE.find(Profile.class)
                .filter(Filters.eq("discordId", id))
                .first();
    }

    /**
     * Get a Profile by his UUID
     */
    public static Profile getProfile(UUID uuid) {
        return DATASTORE.find(Profile.class)
                .filter(Filters.eq("uuid", uuid))
                .first();
    }

    /**
     * Check if Profile exist
     */
    public static boolean exists(String username) {
        return DATASTORE.find(Profile.class)
                .filter(Filters.eq("username", username))
                .first()!=null;
    }

    /**
     * Check if Profile exist
     */
    public static boolean exists(Long discordId) {
        return DATASTORE.find(Profile.class)
                .filter(Filters.eq("discordId", discordId))
                .first()!=null;
    }

    /**
     * Save/Update a Profile
     */
    public static void save(Profile profile) {
        DATASTORE.save(profile);
    }
}