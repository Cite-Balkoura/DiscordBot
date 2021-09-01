package fr.milekat.discordbot.bot.master.features.Moderation;

import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.classes.Ban;
import fr.milekat.discordbot.bot.master.managers.BanManager;
import fr.milekat.discordbot.utils.DateMileKat;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

import javax.annotation.Nonnull;

public class BanUtils {
    public static final String banAcknowledge = "banAcknowledge";
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
