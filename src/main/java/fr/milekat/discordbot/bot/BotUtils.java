package fr.milekat.discordbot.bot;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.managers.RegistrationManager;
import fr.milekat.discordbot.utils.Config;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import org.json.simple.JSONObject;

import java.util.Map;

public class BotUtils {
    /**
     * Replace "<nickname>" with nickname of member
     */
    public static String setNick(Member member, String message) {
        return message.replaceAll("@mention", member.getAsMention())
                .replaceAll("<pseudo>", member.getNickname()==null ? member.getEffectiveName() : member.getNickname());
    }

    /**
     * Method to send an embed to user with ✅/❌ in button
     */
    public static void sendRegister(Member member, MessageEmbed embed) {
        if (RegistrationManager.exists(member.getIdLong())) {
            RegistrationManager.getRegistration(member.getIdLong()).getChannel().sendMessageEmbeds(embed).setActionRow(
                    Button.primary("yes", Emoji.fromMarkdown("<a:Yes:798960396563251221>")).withStyle(ButtonStyle.SUCCESS),
                    Button.primary("no", Emoji.fromMarkdown("<a:No:798960407708303403>")).withStyle(ButtonStyle.DANGER)
            ).queue();
        }
    }

    /**
     * Shortcut to send a message to user in his register channel
     */
    public static void sendRegister(Member member, String string) {
        if (RegistrationManager.exists(member.getIdLong())) {
            RegistrationManager.getRegistration(member.getIdLong()).getChannel().sendMessage(string).queue();
        }
    }

    /**
     * Shortcut to send a message to user in his register channel
     */
    public static void registerAdminAssist(Member member, String string) {
        sendRegister(member, string);
        sendRegister(member, getRole("").getAsMention() + " **HELP !**");
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
