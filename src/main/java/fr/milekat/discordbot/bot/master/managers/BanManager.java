package fr.milekat.discordbot.bot.master.managers;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.classes.Ban;
import fr.milekat.discordbot.bot.master.classes.Profile;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.NoSuchElementException;

public class BanManager {
    private static final Datastore DATASTORE = Main.getDatastore("master");

    /**
     * Check if profile is currently banned (=One of his ban is not acknowledged)
     */
    public static boolean isBanned(Profile profile) {
        return DATASTORE.find(Ban.class).filter(Filters.eq("acknowledge", false)).iterator().toList().
                stream().anyMatch(ban -> ban.getProfile().getId().equals(profile.getId()));
    }

    /**
     * Get last ban of this profile
     */
    public static Ban getLastBan(Profile profile) throws NoSuchElementException {
        return getBans(profile).stream().max(Comparator.comparing(Ban::getLastUpdate)).get();
    }

    /**
     * Get all bans of this profile
     */
    public static ArrayList<Ban> getBans(Profile profile) {
        return new ArrayList<>(DATASTORE.find(Ban.class).filter(Filters.eq("acknowledge", false)).iterator().toList().
                stream().filter(ban -> ban.getProfile().getId().equals(profile.getId())).toList());
    }

    /**
     * Get ban from this TextChannel
     */
    public static Ban getBan(TextChannel channel) {
        return new ArrayList<>(DATASTORE.find(Ban.class)
                .filter(Filters.eq("channelId", channel.getIdLong()))
                .iterator().toList())
                .stream().max(Comparator.comparing(Ban::getLastUpdate)).get();
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
     * Get all members banned
     */
    public static ArrayList<Ban> getBanList() {
        return new ArrayList<>(DATASTORE.find(Ban.class).filter(Filters.eq("acknowledge", false)).iterator().toList());
    }

    /**
     * Save a ban
     */
    public static void save(Ban ban) {
        DATASTORE.save(ban);
    }
}
