package fr.milekat.discordbot.bot.master.Moderation.engines;

import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.Moderation.ModerationUtils;
import fr.milekat.discordbot.bot.master.Moderation.classes.Mute;
import fr.milekat.discordbot.bot.master.Moderation.managers.MuteManager;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MuteEngine {
    public MuteEngine() {
        new Thread(() -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for (Mute mute : MuteManager.getMuteList()) {
                    if (mute.getPardonDate().before(new Date())) {
                        BotUtils.getGuild().retrieveMemberById(mute.getProfile().getDiscordId()).queue(member ->
                                ModerationUtils.unMuteSend(member, mute.getProfile(), BotUtils.getMsg("mute.expired"))
                        );
                    }
                }
            }
        }, 0, 5000)).start();
    }
}
