package fr.milekat.discordbot.bot.events.managers;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.events.classes.Event;
import fr.milekat.discordbot.bot.events.classes.Team;

public class TeamManager {
    private static final Datastore DATASTORE = Main.getDatastore("master");

    /**
     * Get a Team by his name in event
     */
    public static Team getTeam(Event event, String teamName) {
        return DATASTORE.find(Team.class)
                .filter(Filters.eq("event", event))
                .filter(Filters.eq("name", teamName))
                .first();
    }

    /**
     * Check if team exist
     */
    public static boolean exists(Event event, String teamName) {
        return DATASTORE.find(Team.class)
                .filter(Filters.eq("event", event))
                .filter(Filters.eq("name", teamName))
                .first()!=null;
    }

    /**
     * Save/Update a team
     */
    public static void save(Team team) {
        DATASTORE.save(team);
    }
}
