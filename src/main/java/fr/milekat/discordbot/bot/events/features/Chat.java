package fr.milekat.discordbot.bot.events.features;

import fr.milekat.discordbot.bot.events.managers.EventManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class Chat extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getChannel().getParent()==null) return;
        EventManager.getEvents().forEach(mcEvent -> {
            if (event.getChannel().getParent().getIdLong()==mcEvent.getCategoryId()) {

                event.getMessage().delete().queue();
            }
        });
    }
}
