package fr.milekat.discordbot.bot.master.moderation;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.moderation.commands.BanCmd;
import fr.milekat.discordbot.bot.master.moderation.commands.MuteCmd;
import fr.milekat.discordbot.bot.master.moderation.commands.UnBanCmd;
import fr.milekat.discordbot.bot.master.moderation.commands.UnMuteCmd;
import fr.milekat.discordbot.bot.master.moderation.engines.BanEngine;
import fr.milekat.discordbot.bot.master.moderation.engines.MuteEngine;

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
