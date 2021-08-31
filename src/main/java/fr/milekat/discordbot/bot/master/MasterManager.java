package fr.milekat.discordbot.bot.master;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.features.Moderation.BanCommand;
import fr.milekat.discordbot.bot.master.features.Moderation.MuteCommand;
import fr.milekat.discordbot.bot.master.features.ProfileJoin;
import fr.milekat.discordbot.bot.master.features.ProfileRegister;

public class MasterManager {
    public MasterManager() {
        Main.getJDA().addEventListener(new ProfileRegister());
        Main.getJDA().addEventListener(new ProfileJoin());
        Main.getJDA().addEventListener(new MuteCommand());
        Main.getJDA().addEventListener(new BanCommand());
    }
}
