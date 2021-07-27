package fr.milekat.discordbot.core;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.utils.Config;
import net.dv8tion.jda.api.OnlineStatus;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Scanner;

public class Console {
    public Console() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("help")) {
                    sendHelp();
                } else if (input.equalsIgnoreCase("stop")) {
                    stopSequence();
                } else if (input.equalsIgnoreCase("reload")) {
                    try {
                        Config.reloadConfig();
                        Main.log("config.json file reloaded successfully");
                    } catch (IOException | ParseException exception) {
                        Main.log("config.json not found, reload cancelled");
                        if (Main.DEBUG_ERROR) exception.printStackTrace();
                    }
                } else if (input.equalsIgnoreCase("debug")) {
                    debug();
                } else if (input.equalsIgnoreCase("devmode")) {
                    devmode();
                } else {
                    Main.log("Unknown command");
                    sendHelp();
                }
            }
        }
    }

    /**
     * Console help display
     */
    private void sendHelp() {
        Main.log("help: Show this message.");
        Main.log("reload: Reload configs.");
        Main.log("debug: Enable/Disable debug mode.");
        Main.log("stop: Stop bot !");
    }

    /**
     * Disconnect the bot
     */
    private void stopSequence() {
        Main.log("Disconnecting bot...");
        Main.getJDA().getPresence().setStatus(OnlineStatus.OFFLINE);
        Main.log("Good bye!");
        System.exit(0);
    }

    /**
     * Passe en mode debug (throwable Java)
     */
    private void debug() {
        Main.DEBUG_ERROR = !Main.DEBUG_ERROR;
        Main.log("Mode debug: " + Main.DEBUG_ERROR + ".");
    }

    /**
     * Enable / disable dev mode !
     */
    private void devmode() {
        Main.MODE_DEV = !Main.MODE_DEV;
        Main.log("Mode dev: " + Main.MODE_DEV + ".");
    }
}
