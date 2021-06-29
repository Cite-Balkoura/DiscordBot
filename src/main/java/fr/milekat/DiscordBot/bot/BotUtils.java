package fr.milekat.DiscordBot.bot;

import fr.milekat.DiscordBot.Main;
import net.dv8tion.jda.api.entities.*;

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
        TextChannel channel = Main.getJda().getTextChannelById((long) BotManager.getID().get("cGeneral"));
        if (channel!=null) channel.sendMessage(setNick(user, (String) BotManager.getMSG().get("cantMp"))).queue();
    }
}
