package fr.milekat.DiscordBot.bot;

import fr.milekat.DiscordBot.Main;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class BotManager {
    public static JSONObject id;
    public static JSONObject msg;

    public BotManager() {
        id = (JSONObject) Main.getConfig().get("id");
        msg = (JSONObject) Main.getConfig().get("messages");
        /*
        //  Event
        api.addEventListener(new DebugEvent(api, id));
        api.addEventListener(new RegisterEvent(this, api, id, msg));
        api.addEventListener(new TeamEvent(this, api, id, msg));
        api.addEventListener(new McChat(api, id));
        //api.addEventListener(new Chat(this, api, id, msg));
        //api.addEventListener(new Ban(this, api, id, msg));
         */
        if (Main.DEBUG_ERROR) Main.log("Load du bot terminé.");
    }

    /**
     * Reload bot messages from config.json
     */
    public void reloadMsg() {
        try {
            JSONParser jsonParser = new JSONParser();
            FileReader config = new FileReader("config.json");
            msg = (JSONObject) ((JSONObject) jsonParser.parse(config)).get("msg");
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
            id = (JSONObject) ((JSONObject) jsonParser.parse(new FileReader("config.json"))).get("id");
        } catch (IOException | ParseException exception) {
            Main.log("config.json not found");
            if (Main.DEBUG_ERROR) exception.printStackTrace();
        }
    }
}
