package fr.milekat.discordbot.bot.master.managers;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.classes.Mute;
import fr.milekat.discordbot.bot.master.classes.Profile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class MuteManager {
    private static final Datastore DATASTORE = Main.getDatastore("master");

    /**
     * Check if profile is currently muted (=One of his mute is not acknowledged)
     */
    public static boolean isMuted(Profile profile) {
        return DATASTORE.find(Mute.class).filter(Filters.eq("acknowledge", false)).iterator().toList().
                stream().anyMatch(ban -> ban.getProfile().getId().equals(profile.getId()));
    }

    /**
     * Get last mute of this profile
     */
    public static Mute getLastMute(Profile profile) throws NoSuchElementException {
        return getMutes(profile).stream().max(Comparator.comparing(Mute::getLastUpdate)).get();
    }

    /**
     * Get all mutes of this profile
     */
    public static ArrayList<Mute> getMutes(Profile profile) {
        return new ArrayList<>(DATASTORE.find(Mute.class).filter(Filters.eq("acknowledge", false)).iterator().toList().
                stream().filter(ban -> ban.getProfile().getId().equals(profile.getId())).toList());
    }

    /**
     * Get all members muted
     */
    public static ArrayList<Mute> getMuteList() {
        return new ArrayList<>(DATASTORE.find(Mute.class).filter(Filters.eq("acknowledge", false)).iterator().toList());
    }

    /**
     * Save a mute
     */
    public static void save(Mute mute) {
        DATASTORE.save(mute);
    }
}
