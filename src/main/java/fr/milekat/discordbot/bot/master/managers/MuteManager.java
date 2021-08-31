package fr.milekat.discordbot.bot.master.managers;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.classes.Mute;
import fr.milekat.discordbot.bot.master.classes.Profile;

import java.util.Date;

public class MuteManager {
    private static final Datastore DATASTORE = Main.getDatastore("master");

    /**
     * Check if user is currently muted
     */
    public static boolean isMuted(Profile profile) {
        return DATASTORE.find(Mute.class)
                .filter(Filters.and(Filters.lte("muteDate", new Date()), Filters.gte("pardonDate", new Date())))
                .stream().anyMatch(mute -> mute.getProfile().getId().equals(profile.getId()));
    }

    /**
     * Save a mute
     */
    public static void save(Mute mute) {
        DATASTORE.save(mute);
    }
}
