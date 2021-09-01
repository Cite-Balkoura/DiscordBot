package fr.milekat.discordbot.bot.master.features.Moderation;

import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.classes.Mute;
import fr.milekat.discordbot.bot.master.managers.MuteManager;

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
                        MuteManager.save(mute.setAcknowledge(true).setReasonPardon(BotUtils.getMsg("mute.expired")));
                        BotUtils.getGuild().retrieveMemberById(mute.getProfile().getDiscordId()).queue(member ->
                                BotUtils.getGuild().removeRoleFromMember(member, BotUtils.getRole("rMute")).queue()
                        );
                    }
                }
            }
        }, 0, 5000)).start();
    }
}
