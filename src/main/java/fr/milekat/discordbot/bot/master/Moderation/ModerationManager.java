package fr.milekat.discordbot.bot.master.Moderation;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.Moderation.commands.BanCmd;
import fr.milekat.discordbot.bot.master.Moderation.commands.MuteCmd;
import fr.milekat.discordbot.bot.master.Moderation.commands.UnBanCmd;
import fr.milekat.discordbot.bot.master.Moderation.commands.UnMuteCmd;
import fr.milekat.discordbot.bot.master.Moderation.engines.BanEngine;
import fr.milekat.discordbot.bot.master.Moderation.engines.MuteEngine;

public class ModerationManager {
    public ModerationManager() {
        Main.getJDA().addEventListener(new MuteCmd());
        Main.getJDA().addEventListener(new UnMuteCmd());
        new MuteEngine();
        Main.getJDA().addEventListener(new BanCmd());
        Main.getJDA().addEventListener(new UnBanCmd());
        new BanEngine();
    }
}
