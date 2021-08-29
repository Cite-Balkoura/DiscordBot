package fr.milekat.discordbot.bot.master.managers;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.classes.Mute;

import java.util.ArrayList;
import java.util.Date;

public class MuteManager {
    private static final Datastore DATASTORE = Main.getDatastore("master");

    /**
     * Check if user is currently banned
     */
    public static boolean isMuted(Long discordId) {
        return DATASTORE.find(Mute.class)
                .filter(Filters.and(Filters.eq("discordId", discordId),
                        Filters.gte("muteDate", new Date()),
                        Filters.lte("pardonDate", new Date())))
                .first()!=null;
    }

    /**
     * Get mutes for this user
     */
    public static ArrayList<Mute> getCurrentMutes(Long discordId) {
        return new ArrayList<>(DATASTORE.find(Mute.class)
                .filter(Filters.and(Filters.eq("discordId", discordId),
                        Filters.gte("muteDate", new Date()),
                        Filters.lte("pardonDate", new Date())))
                .iterator().toList());
    }
}
