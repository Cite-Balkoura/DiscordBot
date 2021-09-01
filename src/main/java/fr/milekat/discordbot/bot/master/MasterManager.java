package fr.milekat.discordbot.bot.master;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.features.Moderation.*;
import fr.milekat.discordbot.bot.master.features.ProfileJoin;
import fr.milekat.discordbot.bot.master.features.ProfileRegister;

public class MasterManager {
    public MasterManager() {
        Main.getJDA().addEventListener(new ProfileRegister());
        Main.getJDA().addEventListener(new ProfileJoin());
        Main.getJDA().addEventListener(new MuteFeature());
        Main.getJDA().addEventListener(new UnMuteFeature());
        new MuteEngine();
        Main.getJDA().addEventListener(new BanFeature());
        Main.getJDA().addEventListener(new UnBanFeature());
        new BanEngine();
    }
}
