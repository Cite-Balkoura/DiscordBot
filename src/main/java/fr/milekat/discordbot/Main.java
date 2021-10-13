package fr.milekat.discordbot;

import dev.morphia.Datastore;
import fr.milekat.discordbot.bot.BotManager;
import fr.milekat.discordbot.core.Init;
import fr.milekat.discordbot.core.MongoDB;
import fr.milekat.discordbot.core.RabbitMQ;
import fr.milekat.discordbot.utils.WriteLog;
import fr.milekat.utils.DateMileKat;
import net.dv8tion.jda.api.JDA;
import org.slf4j.impl.SimpleLogger;

import java.util.HashMap;

public class Main {
    /* Core */
    private static WriteLog logs;
    public static boolean DEBUG_ERRORS = false;
    public static boolean MODE_DEV = false;
    /* MongoDB */
    private static HashMap<String, Datastore> datastoreMap = new HashMap<>();
    /* Rabbit */
    public static boolean DEBUG_RABBIT = true;
    /* Discord Bot */
    private static JDA JDA;

    /**
     * Main method
     */
    public static void main(String[] args) throws Exception {
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "warn");
        logs = new WriteLog();
        log("Starting application..");
        Init init = new Init();
        //  Console load
        init.getConsole().start();
        //  Load Mongo
        datastoreMap = MongoDB.getDatastoreMap();
        //  Discord bot load
        JDA = init.getJDA();
        new BotManager();
        //  Load RabbitMQ
        new RabbitMQ().getRabbitConsumer().start();
        //  Log
        if (DEBUG_ERRORS) log("Debugs enable");
        if (MODE_DEV) log("Mode dev enable");
        log("Application ready");
    }

    /**
     * Simple log with Date !
     */
    public static void log(String log) {
        System.out.println("[" + DateMileKat.setDateNow() + "] " + log);
        logs.logger("[" + DateMileKat.setDateNow() + "] " + log);
    }

    /**
     * MongoDB Connection (Morphia Datastore) to query
     */
    public static Datastore getDatastore(String dbName) {
        return datastoreMap.get(dbName).startSession();
    }

    /**
     * Discord BOT
     */
    public static JDA getJDA() {
        return JDA;
    }
}
