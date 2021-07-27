package fr.milekat.discordbot.bot;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.events.EventsManager;
import fr.milekat.discordbot.bot.master.MasterManager;
import fr.milekat.discordbot.utils.Config;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Map;

public class BotManager {
    public BotManager() {
        try {
            Config.reloadConfig();
            Main.log("config.json file loaded successfully");
        } catch (IOException | ParseException exception) {
            Main.log("config.json not found");
            if (Main.DEBUG_ERROR) exception.printStackTrace();
        }
        new MasterManager();
        new EventsManager();
        if (Main.DEBUG_ERROR) Main.log("Bot loaded");
    }

    /**
     * Shortcut to reply to user interaction
     */
    public static void reply(GenericInteractionCreateEvent event, String path) {
        event.reply(getMsg(path)).setEphemeral(true).queue();
    }

    /**
     * Shortcut to reply to user interaction with args to replace
     */
    public static void reply(GenericInteractionCreateEvent event, String path, Map<String, String> args) {
        String message = getMsg(path);
        for (Map.Entry<String, String> map : args.entrySet()) {
            message = message.replaceAll(map.getKey(), map.getValue());
        }
        event.reply(message).setEphemeral(true).queue();
    }

    /**
     * Shortcut to get Message from a config section
     */
    public static String getMsg(String path) {
        return getNodeValue(Config.getConfig(), "discord.msg." + path);
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
        return Long.parseLong(getNodeValue(Config.getConfig(), "discord.id." + config));
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
