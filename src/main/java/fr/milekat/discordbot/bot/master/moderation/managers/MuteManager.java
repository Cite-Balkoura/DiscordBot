package fr.milekat.discordbot.bot.master.moderation.managers;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import fr.milekat.discordbot.bot.master.moderation.classes.Mute;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Optional;

public class MuteManager {
    private static final Datastore DATASTORE = Main.getDatastore("master");

    /**
     * Check if profile is currently muted (=One of his mute is not acknowledged)
     */
    public static boolean isMuted(Profile profile) {
        return DATASTORE.find(Mute.class).filter(Filters.eq("acknowledge", false)).iterator().toList().
                stream().anyMatch(mute -> mute.getProfile().getId().equals(profile.getId()));
    }

    /**
     * Get last mute of this profile
     */
    public static Mute getLastMute(Profile profile) throws NoSuchElementException {
        Optional<Mute> optionalMute = getMutes(profile).stream().max(Comparator.comparing(Mute::getLastUpdate));
        return optionalMute.orElse(null);
    }

    /**
     * Get all mutes of this profile
     */
    public static ArrayList<Mute> getMutes(Profile profile) {
        return new ArrayList<>(DATASTORE.find(Mute.class).filter(Filters.eq("acknowledge", false)).iterator().toList().
                stream().filter(mute -> mute.getProfile().getId().equals(profile.getId())).toList());
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
