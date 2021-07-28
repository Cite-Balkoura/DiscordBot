package fr.milekat.discordbot.bot;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.events.EventsManager;
import fr.milekat.discordbot.bot.master.MasterManager;
import fr.milekat.discordbot.utils.Config;
import org.json.simple.parser.ParseException;

import java.io.IOException;

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
}
