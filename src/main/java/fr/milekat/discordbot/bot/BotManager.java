package fr.milekat.discordbot.bot;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.events.EventsManager;
import fr.milekat.discordbot.bot.master.MasterManager;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
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
        new MasterManager();
        new EventsManager();
        if (Main.DEBUG_ERROR) Main.log("Bot loaded");
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
     * Shortcut to get Message from a config section
     */
    public static String getMsg(String path) {
        return (String) getNodeValue(MSG, path);
    }

    /**
     * Shortcut to get Guild from config section
     */
    public static Guild getGuild() {
        return Main.getJDA().getGuildById((long) getNodeValue(ID, "gPublic"));
    }

    /**
     * Shortcut to get Category from a config section
     */
    public static Category getCategory(String path) {
        return Main.getJDA().getCategoryById((long) getNodeValue(ID, path));
    }

    /**
     * Shortcut to get TextChannel from a config section
     */
    public static TextChannel getChannel(String path) {
        return Main.getJDA().getTextChannelById((long) getNodeValue(ID, path));
    }

    /**
     * Shortcut to get TextChannel from a config section
     */
    public static Role getRole(String path) {
        return Main.getJDA().getRoleById((long) getNodeValue(ID, path));
    }

    /**
     * Return the value of node (As object)
     */
    private static Object getNodeValue(JSONObject type, String path) {
        JSONObject jsonObject = type;
        String loop = path.substring(path.lastIndexOf('.'));
        for (String node : loop.split("\\.")) {
            jsonObject = (JSONObject) jsonObject.get(node);
        }
        return jsonObject.get(path.replaceAll(loop, ""));
    }
}
