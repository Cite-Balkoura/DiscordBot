package fr.milekat.DiscordBot.bot;

import fr.milekat.DiscordBot.Main;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class BotManager {
    private static JSONObject ID;
    private static JSONObject MSG;

    public BotManager() {
        ID = (JSONObject) Main.getConfig().get("id");
        MSG = (JSONObject) Main.getConfig().get("messages");
        /*
        //  Event
        api.addEventListener(new EventClass...);
         */
        if (Main.DEBUG_ERROR) Main.log("Bot loaded.");
    }

    /**
     * Reload bot messages from config.json
     */
    public void reloadMsg() {
        try {
            JSONParser jsonParser = new JSONParser();
            MSG = (JSONObject) ((JSONObject)((JSONObject) jsonParser.parse(new FileReader("config.json"))).get("discord")).get("msg");
        } catch (IOException | ParseException exception) {
            Main.log("config.json not found");
            if (Main.DEBUG_ERROR) exception.printStackTrace();
        }
    }

    /**
     * Reload bot channels id from config.json
     */
    public void reloadCh() {
        try {
            JSONParser jsonParser = new JSONParser();
            ID = (JSONObject) ((JSONObject)((JSONObject) jsonParser.parse(new FileReader("config.json"))).get("discord")).get("id");
        } catch (IOException | ParseException exception) {
            Main.log("config.json not found");
            if (Main.DEBUG_ERROR) exception.printStackTrace();
        }
    }

    /**
     * Shortcut to get messages from config section
     */
    public static JSONObject getMSG() {
        return MSG;
    }

    /**
     * Shortcut to get ids from config section
     */
    public static JSONObject getID() {
        return ID;
    }
}
