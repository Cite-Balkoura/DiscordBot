package fr.milekat.discordbot.bot.master.managers;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.classes.Step;

import java.util.ArrayList;

public class StepManager {
    private static final Datastore DATASTORE = Main.getDatastore("master");

    /**
     * Get all steps
     */
    public static ArrayList<Step> getSteps() {
        return new ArrayList<>(DATASTORE.find(Step.class).iterator().toList());
    }

    /**
     * Get a Step by his name
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
