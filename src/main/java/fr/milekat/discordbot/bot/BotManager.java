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
    private static JSONObject CONFIG;

    public BotManager() {
        reloadConfig();
        new MasterManager();
        new EventsManager();
        if (Main.DEBUG_ERROR) Main.log("Bot loaded");
    }

    /**
     * Reload config.json file
     */
    public void reloadConfig() {
        try {
            CONFIG = ((JSONObject) new JSONParser().parse(new FileReader("config.json")));
        } catch (IOException | ParseException exception) {
            Main.log("config.json not found");
            if (Main.DEBUG_ERROR) exception.printStackTrace();
        }
    }

    /**
     * Shortcut to get Message from a config section
     */
    public static String getMsg(String path) {
        return getNodeValue(CONFIG, "discord.msg." + path);
    }

    /**
     * Shortcut to get Guild from config section
     */
    public static Guild getGuild() {
        return Main.getJDA().getGuildById(getId("gPublic"));
    }

    /**
     * Shortcut to get Category from a config section
     */
    public static Category getCategory(String path) {
        return Main.getJDA().getCategoryById(getId(path));
    }

    /**
     * Shortcut to get TextChannel from a config section
     */
    public static TextChannel getChannel(String path) {
        return Main.getJDA().getTextChannelById(getId(path));
    }

    /**
     * Shortcut to get TextChannel from a config section
     */
    public static Role getRole(String path) {
        return Main.getJDA().getRoleById(getId(path));
    }

    /**
     * Get discord id from config.json file
     */
    private static Long getId(String config) {
        return Long.parseLong(getNodeValue(CONFIG, "discord.id." + config));
    }

    /**
     * Return the value of node
     */
    private static String getNodeValue(JSONObject objectFile, String path) {
        JSONObject jsonObject = objectFile;
        for (String node : path.substring(0, path.lastIndexOf('.')).split("\\.")) {
            jsonObject = (JSONObject) jsonObject.get(node);
        }
        return jsonObject.get(path.substring(path.lastIndexOf('.') + 1)).toString();
    }
}
