package fr.milekat.discordbot.bot.master.features.Moderation;

import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.classes.Ban;
import fr.milekat.discordbot.bot.master.managers.BanManager;

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
                        BanManager.save(ban.setReasonPardon(BotUtils.getMsg("ban.expired")).setAcknowledge(true));
                        BotUtils.getGuild().retrieveMemberById(ban.getProfile().getDiscordId()).queue(member ->
                                BanUtils.unBanNotify(member, ban)
                        );
                    }
                }
            }
        }, 0, 5000)).start();
    }
}
