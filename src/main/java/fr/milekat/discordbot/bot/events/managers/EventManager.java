package fr.milekat.discordbot.bot.events.managers;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.events.classes.Event;

import java.util.ArrayList;

public class EventManager {
    private static final Datastore DATASTORE = Main.getDatastore("master");

    /**
     * Get an Event by his name
     */
    public static Event getEvent(String eventName) {
        return DATASTORE.find(Event.class)
                .filter(Filters.eq("name", eventName))
                .first();
    }

    /**
     * Get an Event by Main Category id
     */
    public static Event getEventCtMain(Long eventCategory) {
        return DATASTORE.find(Event.class)
                .filter(Filters.eq("categoryId", eventCategory))
                .first();
    }

    /**
     * Get an Event by Team Category id
     */
    public static Event getEventCtTeam(Long categoryTeamId) {
        return DATASTORE.find(Event.class)
                .filter(Filters.eq("categoryTeamId", categoryTeamId))
                .first();
    }

    /**
     * Get all events
     */
    public static ArrayList<Event> getEvents() {
        return new ArrayList<>(DATASTORE.find(Event.class).iterator().toList());
    }
}
