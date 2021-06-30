package fr.milekat.discordbot.bot.master;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.features.ProfileRegister;

public class MasterManager {
    public MasterManager() {
        Main.getJDA().addEventListener(new ProfileRegister());
    }
}
