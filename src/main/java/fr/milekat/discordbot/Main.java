package fr.milekat.discordbot;

import dev.morphia.Datastore;
import fr.milekat.discordbot.bot.BotManager;
import fr.milekat.discordbot.core.Init;
import fr.milekat.discordbot.core.RabbitMQ;
import fr.milekat.discordbot.utils.DateMileKat;
import fr.milekat.discordbot.utils.WriteLog;
import net.dv8tion.jda.api.JDA;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class Main {
    /* Core */
    private static WriteLog logs;
    public static boolean DEBUG_ERROR = false;
    public static boolean MODE_DEV = false;
    private static JSONObject configs;
    /* MongoDB */
    private static HashMap<String, Datastore> datastoreMap = new HashMap<>();
    /* Rabbit */
    public static boolean DEBUG_RABBIT = true;
    /* Discord Bot */
    private static JDA JDA;
    private static BotManager BOT;

    /**
     * Main method
     */
    public static void main(String[] args) throws Exception {
        logs = new WriteLog();
        log("Starting application..");
        Init init = new Init();
        //  Console load
        init.getConsole().start();
        //  Config load
        configs = init.getConfigs();
        //  Load Mongo
        datastoreMap = init.getDatastoreMap();
        //  Load RabbitMQ
        new RabbitMQ().getRabbitConsumer().start();
        //  Discord bot load
        JDA = init.getJDA();
        BOT = new BotManager();
        //  Log
        if (DEBUG_ERROR) log("Debugs enable");
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
     * "config.json" file
     */
    public static JSONObject getConfig() {
        return configs;
    }

    /**
     * MongoDB Connection (Morphia Datastore) to query
     */
    public static Datastore getDatastore(String dbName) {
        /*if (!datastoreMap.containsKey(dbName)) {
            Main.log("[Error] Datastore '" + dbName + "' not found");
            return null;
        }*/
        return datastoreMap.get(dbName);
    }

    /**
     * Discord BOT
     */
    public static JDA getJDA() {
        return JDA;
    }

    /**
     * BOT Manager
     */
    public static BotManager getBotManager() {
        return BOT;
    }
}
