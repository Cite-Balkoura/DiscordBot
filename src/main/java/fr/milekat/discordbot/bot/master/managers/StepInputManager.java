package fr.milekat.discordbot.bot.master.managers;

import dev.morphia.Datastore;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.classes.StepInput;

public class StepInputManager {
    private static final Datastore DATASTORE = Main.getDatastore("master");

    /**
     * Save/Update a StepInput
     */
    public static void save(StepInput stepInput) {
        DATASTORE.save(stepInput);
    }
}
