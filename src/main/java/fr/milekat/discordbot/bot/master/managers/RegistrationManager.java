package fr.milekat.discordbot.bot.master.managers;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.classes.Registration;

public class RegistrationManager {
    private static final Datastore DATASTORE = Main.getDatastore("master");

    /**
     * Get a Registration by his username
     */
    public static Registration getRegistration(String username) {
        return DATASTORE.find(Registration.class)
                .filter(Filters.eq("username", username))
                .first();
    }

    /**
     * Get a Registration by his discordId
     */
    public static Registration getRegistration(Long discordId) {
        return DATASTORE.find(Registration.class)
                .filter(Filters.eq("discordId", discordId))
                .first();
    }

    /**
     * Check if channel is a Registration channel
     */
    public static boolean isRegistration(Long registerChannelId) {
        return DATASTORE.find(Registration.class)
                .filter(Filters.eq("registerChannelId", registerChannelId))
                .first()!=null;
    }

    /**
     * Check if Registration exist
     */
    public static boolean exists(Long discordId) {
        return DATASTORE.find(Registration.class)
                .filter(Filters.eq("discordId", discordId))
                .first()!=null;
    }

    /**
     * Save/Update a Registration
     */
    public static void save(Registration registration) {
        DATASTORE.save(registration);
    }
}