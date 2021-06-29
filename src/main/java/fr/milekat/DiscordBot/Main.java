package fr.milekat.DiscordBot;

import dev.morphia.Datastore;
import fr.milekat.DiscordBot.bot.BotManager;
import fr.milekat.DiscordBot.core.Init;
import fr.milekat.DiscordBot.utils.DateMileKat;
import fr.milekat.DiscordBot.utils.MariaManage;
import fr.milekat.DiscordBot.utils.WriteLog;
import net.dv8tion.jda.api.JDA;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.util.HashMap;

public class Main {
    /* Core */
    private static WriteLog logs;
    public static boolean DEBUG_ERROR = false;
    public static boolean MODE_DEV = false;
    private static JSONObject configs;
    /* SQL */
    private static MariaManage mariaManage;
    /* MongoDB */
    private static HashMap<String, Datastore> datastoreMap;
    /* Jedis */
    public static boolean DEBUG_JEDIS = true;
    /* Discord Bot */
    private static JDA jda;
    private static BotManager bot;

    /**
     * Main method
     */
    public static void main(String[] args) throws Exception {
        logs = new WriteLog();
        log("Starting application..");
        Init init = new Init();
        //  Console load
        init.getConsole().start();
        configs = init.getConfigs();
        //  Load SQL + Launch ping + DATES
        mariaManage = init.setSQL();
        //  Load Mongo
        datastoreMap = init.getDatastoreMap();
        //  Discord bot load
        jda = init.getJDA();
        bot = new BotManager();
        //  Log
        if (DEBUG_ERROR) log("Debugs enable");
        if (MODE_DEV) log("Mode dev enable");
        log("Application ready.");
    }

    /**
     * Simple log with Date !
     *
     * @param log message to send
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
     * SQL Connection to make queries
     */
    public static Connection getSql() {
        return mariaManage.getConnection();
    }

    /**
     * MongoDB Connection (Morphia Datastore) to query
     */
    public static Datastore getDatastore(String dbName) {
        return datastoreMap.get(dbName);
    }

    /**
     * Discord BOT
     */
    public static JDA getJda() {
        return jda;
    }

    /**
     * BOT Manager
     */
    public static BotManager getBotManager() {
        return bot;
    }
}
