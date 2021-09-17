package fr.milekat.discordbot.bot.master.Moderation;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.Moderation.classes.Ban;
import fr.milekat.discordbot.bot.master.Moderation.classes.Mute;
import fr.milekat.discordbot.bot.master.Moderation.managers.BanManager;
import fr.milekat.discordbot.bot.master.Moderation.managers.MuteManager;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import fr.milekat.discordbot.bot.master.core.managers.ProfileManager;
import fr.milekat.discordbot.core.RabbitMQ;
import fr.milekat.discordbot.utils.DateMileKat;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class ModerationUtils {
    public static final String banAcknowledge = "banAcknowledge";

    /**
     * Check if command can be processed
     */
    public static boolean cantProcess(SlashCommandEvent event) {
        if (event.getUser().isBot() || !event.getGuild().equals(BotUtils.getGuild())) return true;
        if (!event.getMember().getRoles().contains(BotUtils.getRole("rAdmin"))) {
            event.reply(BotUtils.getMsg("noPerm")).setEphemeral(true).queue();
            return true;
        }
        //  Check if target has a profile
        if (!ProfileManager.exists(event.getOption("member").getAsMember().getIdLong())) {
            event.reply(BotUtils.getMsg("mute.slashNoProfile")).setEphemeral(true).queue();
            return true;
        }
        return false;
    }

    /**
     * Check if command can be processed (with delay arg)
     */
    public static boolean cantProcessDelay(SlashCommandEvent event) {
        if (cantProcess(event)) return true;
        //  Check if delay less than 10s (10000ms)
        if (DateMileKat.parsePeriod(event.getOption("duration").getAsString()) + new Date().getTime() <
                (new Date().getTime()+10000)) {
            event.reply(BotUtils.getMsg("mute.slashDelayToLow")).setEphemeral(true).queue();
            return true;
        }
        return false;
    }

    /**
     * Process a mute of a player (Notify player)
     */
    public static void mute(Member targetMember, Profile target, Profile sender, Long delay, String reason) {
        BotUtils.getGuild().addRoleToMember(targetMember, BotUtils.getRole("rMute")).queue();
        Profile targetProfile = ProfileManager.getProfile(targetMember.getIdLong());
        if (MuteManager.isMuted(targetProfile)) {
            Mute oldMute = MuteManager.getLastMute(targetProfile);
            MuteManager.save(oldMute.setAcknowledge(true));
            MuteManager.save(new Mute(oldMute.getProfile(), oldMute.getMuteDate(), new Date(delay), reason));
        } else {
            MuteManager.save(new Mute(targetProfile, new Date(), new Date(delay), reason));
        }
        Main.log(target.getUsername() + " a été mute par " + sender.getUsername() + " pour " + reason);
    }

    /**
     * Process a mute of a player (Notify player and send into Rabbit)
     */
    public static void muteSend(Member targetMember, Profile target, Profile sender, Long delay, String reason) {
        mute(targetMember, target, sender, delay, reason);
        try {
            RabbitMQ.rabbitSend(String.format("""
                        {
                            "type": "mute",
                            "target": "%s",
                            "sender": "%s",
                            "delay": "%s",
                            "reason": "%s"
                        }""", target.getUuid(), sender.getUuid(), delay, reason));
        } catch (IOException | TimeoutException exception) {
            Main.log("[Error] RabbitSend - mute");
            if (Main.DEBUG_ERRORS) exception.printStackTrace();
        }
    }

    /**
     * Process an unMute of a player
     */
    public static void unMute(Member targetMember, Profile target, String reason) {
        if (MuteManager.isMuted(target)) {
            MuteManager.save(MuteManager.getLastMute(target)
                    .setPardonDate(new Date()).setReasonPardon(reason).setAcknowledge(true));
            BotUtils.getGuild().removeRoleFromMember(targetMember, BotUtils.getRole("rMute")).queue();
            Main.log(target.getUsername() + " n'est plus mute !");
        } else {
            Main.log("Not muted");
        }
    }

    /**
     * Process an unMute of a player
     */
    public static void unMuteSend(Member targetMember, Profile target, String reason) {
        unMute(targetMember, target, reason);
        try {
            RabbitMQ.rabbitSend(String.format("""
                        {
                            "type": "unmute",
                            "target": "%s",
                            "reason": "%s"
                        }""", target.getUuid(), reason));
        } catch (IOException | TimeoutException exception) {
            Main.log("[Error] RabbitSend - unmute");
            if (Main.DEBUG_ERRORS) exception.printStackTrace();
        }
    }

    /**
     * Process a ban of a player
     */
    public static void ban(Member targetMember, Profile target, Profile sender, Long delay, String reason) {
        BotUtils.getGuild().removeRoleFromMember(targetMember, BotUtils.getRole("rProfile")).queue();
        BotUtils.getGuild().addRoleToMember(targetMember, BotUtils.getRole("rBan")).queue();
        Profile targetProfile = ProfileManager.getProfile(targetMember.getIdLong());
        if (BanManager.isBanned(targetProfile)) {
            Ban oldBan = BanManager.getLastBan(targetProfile);
            Ban updatedBan = new Ban(oldBan.getProfile(), oldBan.getBanDate(), new Date(delay), reason);
            if (oldBan.getChannel()!=null && !BotUtils.getGuild().getTextChannels().contains(oldBan.getChannel())) {
                ModerationUtils.sendBan(targetMember, updatedBan);
            } else if (oldBan.getChannel()!=null) {
                updatedBan.setChannel(oldBan.getChannel());
                oldBan.getChannel().sendMessage(BotUtils.getMsg("ban.informUpdate")
                        .replaceAll("<mention>", targetMember.getAsMention())
                        .replaceAll("<reason>", updatedBan.getReasonBan())
                        .replaceAll("<ban-delay>", DateMileKat.reamingToString(updatedBan.getPardonDate()))
                        .replaceAll("<pardon-date>", DateMileKat.getDate(updatedBan.getPardonDate()))
                ).queue();
            }
            BanManager.save(oldBan.setAcknowledge(true));
            BanManager.save(updatedBan);
        } else {
            ModerationUtils.sendBan(targetMember, new Ban(targetProfile, new Date(), new Date(delay), reason));
        }
        Main.log(target.getUsername() + " a été ban par " + sender.getUsername() + " pour " + reason);
    }

    /**
     * Notify servers with ban of a player
     */
    public static void banSend(Member targetMember, Profile target, Profile sender, Long delay, String reason) {
        ban(targetMember, target, sender, delay, reason);
        try {
            RabbitMQ.rabbitSend(String.format("""
                        {
                            "type": "ban",
                            "target": "%s",
                            "sender": "%s",
                            "delay": "%s",
                            "reason": "%s"
                        }""", target.getUuid(), sender.getUuid(), delay, reason));
        } catch (IOException | TimeoutException exception) {
            Main.log("[Error] RabbitSend - ban");
            if (Main.DEBUG_ERRORS) exception.printStackTrace();
        }
    }

    /**
     * Process an unBan of a player
     */
    public static void unBan(Member targetMember, Profile target, String reason) {
        if (BanManager.isBanned(target)) {
            Ban oldBan = BanManager.getLastBan(target);
            Ban updatedBan = new Ban(target, oldBan.getBanDate(), new Date(), oldBan.getReasonBan());
            BanManager.save(oldBan.setAcknowledge(true));
            BanManager.save(updatedBan.setReasonPardon(reason).setAcknowledge(true));
            ModerationUtils.unBanNotify(targetMember, updatedBan);
            Main.log(target.getUsername() + " n'est plus ban !");
        } else {
            Main.log("Not banned");
        }
    }

    /**
     * Process an unBan of a player
     */
    public static void unBanSend(Member targetMember, Profile target, String reason) {
        unBan(targetMember, target, reason);
        try {
            RabbitMQ.rabbitSend(String.format("""
                        {
                            "type": "unban",
                            "target": "%s",
                            "reason": "%s"
                        }""", target.getUuid(), reason));
        } catch (IOException | TimeoutException exception) {
            Main.log("[Error] RabbitSend - unban");
            if (Main.DEBUG_ERRORS) exception.printStackTrace();
        }
    }

    /**
     * Create ban channel
     */
    public static void sendBan(@Nonnull Member member, Ban ban) {
        BotUtils.getGuild().createTextChannel("ban-" + member.getEffectiveName(), BotUtils.getCategory("ccBan"))
                .queue(textChannel -> {
                    BanManager.save(ban.setChannel(textChannel));
                    textChannel.putPermissionOverride(member).setAllow(Permission.VIEW_CHANNEL).queue();
                    textChannel.sendMessage(BotUtils.getMsg("ban.informMessage")
                            .replaceAll("<mention>", member.getAsMention())
                            .replaceAll("<reason>", ban.getReasonBan())
                            .replaceAll("<ban-delay>", DateMileKat.reamingToString(ban.getPardonDate()))
                            .replaceAll("<pardon-date>", DateMileKat.getDate(ban.getPardonDate()))
                    ).queue();
                });
    }

    /**
     * Notify a member of his unBan
     */
    public static void unBanNotify(@Nonnull Member member, Ban ban) {
        ban.getChannel().sendMessage(BotUtils.getMsg("ban.unBanNotify")
                        .replaceAll("<mention>", member.getAsMention())
                .replaceAll("<reason>", ban.getReasonPardon()))
                .setActionRow(Button.success(banAcknowledge, BotUtils.getMsg("ban.buttonAcknowledge"))
                        .withStyle(ButtonStyle.PRIMARY))
                .queue();
    }

    /**
     * Process an unBan (Delete channel, remove role, give profile role, etc..)
     */
    public static void unBanProcess(@Nonnull Member member, Ban ban) {
        ban.getChannel().delete().queue();
        BotUtils.getGuild().addRoleToMember(member, BotUtils.getRole("rProfile")).queue();
        BotUtils.getGuild().removeRoleFromMember(member, BotUtils.getRole("rBan")).queue();
    }
}
