package fr.milekat.discordbot.bot.master.managers;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.classes.Ban;

import java.util.ArrayList;
import java.util.Date;

public class BanManager {
    private static final Datastore DATASTORE = Main.getDatastore("master");

    /**
     * Check if user is currently banned
     */
    public static boolean isBanned(Long discordId) {
        return DATASTORE.find(Ban.class)
                .filter(Filters.and(Filters.eq("discordId", discordId),
                        Filters.gte("banDate", new Date()),
                        Filters.lte("pardonDate", new Date())))
                .first()!=null;
    }

    /**
     * Get bans for this user
     */
    public static ArrayList<Ban> getCurrentBans(Long discordId) {
        return new ArrayList<>(DATASTORE.find(Ban.class)
                .filter(Filters.and(Filters.eq("discordId", discordId),
                        Filters.gte("banDate", new Date()),
                        Filters.lte("pardonDate", new Date())))
                .iterator().toList());
    }
}
