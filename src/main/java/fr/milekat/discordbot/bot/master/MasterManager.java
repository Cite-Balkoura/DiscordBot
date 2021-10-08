package fr.milekat.discordbot.bot.master;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.core.features.ProfileJoin;
import fr.milekat.discordbot.bot.master.core.features.ProfileRegister;
import fr.milekat.discordbot.bot.master.moderation.ModerationManager;

public class MasterManager {
    public MasterManager() {
        Main.getJDA().addEventListener(new ProfileRegister());
        Main.getJDA().addEventListener(new ProfileJoin());
        new ModerationManager();
    }
}
