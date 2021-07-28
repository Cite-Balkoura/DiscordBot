package fr.milekat.discordbot.bot.master.managers;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.classes.Step;

public class StepManager {
    private static final Datastore DATASTORE = Main.getDatastore("master");

    /**
     * Get a player by his discord id
     */
    public static Step getStep(String stepName) {
        return DATASTORE.find(Step.class)
                .filter(Filters.eq("name", stepName))
                .first();
    }

    /**
     * Check if team exist
     */
    public static boolean exists(String stepName) {
        return DATASTORE.find(Step.class)
                .filter(Filters.eq("name", stepName))
                .first()!=null;
    }
}
