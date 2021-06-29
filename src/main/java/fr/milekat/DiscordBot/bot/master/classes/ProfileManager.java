package fr.milekat.DiscordBot.bot.master.classes;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.DiscordBot.Main;

public class ProfileManager {
    private static final Datastore DATASTORE = Main.getDatastore("master");

    /**
     * Get a player by his discord id
     */
    public static Profile getProfile(Long id) {
        return DATASTORE.find(Profile.class)
                .filter(Filters.eq("discordId", id))
                .first();
    }
}
