package fr.milekat.discordbot.bot.master.moderation.engines;

import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.moderation.ModerationUtils;
import fr.milekat.discordbot.bot.master.moderation.classes.Mute;
import fr.milekat.discordbot.bot.master.moderation.managers.MuteManager;

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
        }, 0, 2000)).start();
    }
}
