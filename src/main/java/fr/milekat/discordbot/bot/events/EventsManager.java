package fr.milekat.discordbot.bot.events;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.events.features.Chat;
import fr.milekat.discordbot.bot.events.features.JoinEvent;
import fr.milekat.discordbot.bot.events.features.PlayerReJoin;
import fr.milekat.discordbot.bot.events.features.TeamBuilder;

public class EventsManager {
    public EventsManager() {
        Main.getJDA().addEventListener(new Chat());
        Main.getJDA().addEventListener(new JoinEvent());
        Main.getJDA().addEventListener(new PlayerReJoin());
        Main.getJDA().addEventListener(new TeamBuilder());
    }
}
