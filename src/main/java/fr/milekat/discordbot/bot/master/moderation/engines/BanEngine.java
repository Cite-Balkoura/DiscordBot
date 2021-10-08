package fr.milekat.discordbot.bot.master.moderation.engines;

import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.moderation.ModerationUtils;
import fr.milekat.discordbot.bot.master.moderation.classes.Ban;
import fr.milekat.discordbot.bot.master.moderation.managers.BanManager;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class BanEngine {
    public BanEngine() {
        new Thread(() -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for (Ban ban : BanManager.getBanList()) {
                    if (ban.getPardonDate().before(new Date())) {
                        BotUtils.getGuild().retrieveMemberById(ban.getProfile().getDiscordId()).queue(member ->
                                ModerationUtils.unBanSend(member, ban.getProfile(), BotUtils.getMsg("ban.expired"))
                        );
                    }
                }
            }
        }, 0, 2000)).start();
    }
}
