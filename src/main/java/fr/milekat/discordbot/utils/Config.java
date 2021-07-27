package fr.milekat.discordbot.utils;

import fr.milekat.discordbot.Main;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class Config {
    private static JSONObject CONFIG = null;

    /**
     * Load the config file (config.json)
     */
    public static JSONObject loadConfig() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject configs = (JSONObject) jsonParser.parse(new FileReader("config.json"));
        Main.DEBUG_ERROR = (boolean) ((JSONObject) configs.get("config")).get("debugError");
        Main.DEBUG_RABBIT = (boolean) ((JSONObject) configs.get("config")).get("debugRabbitMQ");
        Main.MODE_DEV = (boolean) ((JSONObject) configs.get("config")).get("devMode");
        return configs;
    }

    /**
     * Get config JSONObject
     */
    public static JSONObject getConfig() {
        if (CONFIG==null) {
            try {
                CONFIG = loadConfig();
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        return CONFIG;
    }

    /**
     * Reload config.json file
     */
    public static void reloadConfig() throws IOException, ParseException {
        CONFIG = ((JSONObject) new JSONParser().parse(new FileReader("config.json")));
    }
}
