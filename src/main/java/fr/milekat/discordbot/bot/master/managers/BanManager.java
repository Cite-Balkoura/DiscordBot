package fr.milekat.discordbot.bot.master.managers;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.classes.Ban;
import fr.milekat.discordbot.bot.master.classes.Profile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class BanManager {
    private static final Datastore DATASTORE = Main.getDatastore("master");

    /**
     * Check if profile is currently banned (=His last updated ban is started and not finished)
     */
    public static boolean isBanned(Profile profile) {
        Ban ban = getLastBan(profile);
        return ban.getBanDate().getTime() <= new Date().getTime() && ban.getPardonDate().getTime() >= new Date().getTime();
    }

    /**
     * Get last ban of this profile
     */
    public static Ban getLastBan(Profile profile) {
        return getBans(profile).stream().max(Comparator.comparing(Ban::getLastUpdate)).get();
    }

    /**
     * Get all bans of this profile
     */
    public static ArrayList<Ban> getBans(Profile profile) {
        return new ArrayList<>(DATASTORE.find(Ban.class).iterator().toList().
                stream().filter(ban -> ban.getProfile().getId().equals(profile.getId())).toList());
    }

    /**
     * Get all bans update from a ban
     */
    public static ArrayList<Ban> getFullBan(Profile profile, Ban ban) {
        return getFullBan(profile, ban.getBanDate());
    }

    /**
     * Get all bans update from a banDate
     */
    public static ArrayList<Ban> getFullBan(Profile profile, Date banDate) {
        return new ArrayList<>(DATASTORE.find(Ban.class)
                .filter(Filters.eq("banDate", banDate))
                .stream().filter(ban -> ban.getProfile().getId().equals(profile.getId()))
                .sorted(Comparator.comparing(Ban::getLastUpdate).reversed()).toList());
    }

    /**
     * Save a ban
     */
    public static void save(Ban ban) {
        DATASTORE.save(ban);
    }
}
