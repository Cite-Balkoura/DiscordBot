package fr.milekat.discordbot.bot;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.utils.Config;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import org.json.simple.JSONObject;

import java.util.Map;

public class BotUtils {
    /**
     * Replace "@mention" with mentioned user
     */
    public static String setNick(User user, String message) {
        return message.replaceAll("@mention", user.getAsMention()).replaceAll("<pseudo>", user.getName());
    }

    /**
     * Replace "<nickname>" with nickname of member
     */
    public static String setNick(Member member, String message) {
        return message.replaceAll("@mention", member.getAsMention())
                .replaceAll("<pseudo>", member.getNickname()==null ? member.getEffectiveName() : member.getNickname());
    }

    /**
     * Method to send an embed in channel with ✅/❌
     */
    public static void sendEmbed(MessageChannel channel, MessageEmbed embed) {
        channel.sendMessageEmbeds(embed).queue(message ->
                message.addReaction("✅").queue(reaction ->
                        message.addReaction("❌").queue()));
    }

    /**
     * Method to send an embed to user with ✅/❌
     */
    public static void sendPrivate(User user, MessageEmbed embed) {
        user.openPrivateChannel().queue(privateChannel ->
                        privateChannel.sendMessageEmbeds(embed).queue(message ->
                                message.addReaction("✅").queue(reaction ->
                                        message.addReaction("❌").queue())),
                throwable -> cantSendPrivate(user)
        );
    }

    /**
     * Method to send a simple private message to user
     */
    public static void sendPrivate(User user, String message) {
        user.openPrivateChannel().queue(
                privateChannel -> privateChannel.sendMessage(setNick(user, message)).queue(),
                throwable -> cantSendPrivate(user)
        );
    }

    /**
     * If the bot got an issue when sending a private message to a User
     */
    private static void cantSendPrivate(User user) {
        TextChannel channel = BotUtils.getChannel("cGeneral");
        if (channel!=null) channel.sendMessage(setNick(user, BotUtils.getMsg("cantMp"))).queue();
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
