package fr.milekat.discordbot.bot.master;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.Moderation.ModerationManager;
import fr.milekat.discordbot.bot.master.core.features.ProfileJoin;
import fr.milekat.discordbot.bot.master.core.features.ProfileRegister;

public class MasterManager {
    public MasterManager() {
        Main.getJDA().addEventListener(new ProfileRegister());
        Main.getJDA().addEventListener(new ProfileJoin());
        new ModerationManager();
    }
}
