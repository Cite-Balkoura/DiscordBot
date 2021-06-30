package fr.milekat.discordbot.bot.events.features;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotManager;
import fr.milekat.discordbot.bot.events.classes.Event;
import fr.milekat.discordbot.bot.events.classes.EventManager;
import fr.milekat.discordbot.bot.events.classes.Participation;
import fr.milekat.discordbot.bot.events.classes.ParticipationManager;
import fr.milekat.discordbot.bot.master.classes.Profile;
import fr.milekat.discordbot.bot.master.classes.ProfileManager;
import fr.milekat.discordbot.utils.DateMileKat;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinEvent extends ListenerAdapter {
    public void onGuildMemberJoin(MessageReactionAddEvent event) {
        if (!event.getReaction().getReactionEmote().getName().equalsIgnoreCase(":white_check_mark:")) return;
        if (!event.getChannel().equals(Main.getJDA().getTextChannelById((long) BotManager.getID().get("cEvent")))) return;
        event.retrieveUser().queue(user -> event.retrieveMessage().queue(message -> event.retrieveMember().queue(member -> addPlayerEventReg(member, user, message))));
    }

    public void addPlayerEventReg(Member member, User user, Message message) {
        if (user.isBot() || message.getEmbeds().isEmpty()) return;
        //  Prevent a banned player to register to an event if he is currently banned
        if (member.getRoles().contains(Main.getJDA().getRoleById((long) BotManager.getID().get("rBan")))) return;
        Profile profile = ProfileManager.getProfile(user.getIdLong());
        Event event = EventManager.getEvent(message.getEmbeds().get(0).getTitle());
        //  Save his participation to the event
        ParticipationManager.save(new Participation(profile.getUuid(), event));
        user.openPrivateChannel().queue(privateChannel -> {
            // TODO: 29/06/2021 Find a way to put this method into an util class (To be more generic)
            privateChannel.sendMessage(BotManager.getMSG().get("joinEvent").toString().replaceAll("<EVENT_NAME>", event.getName())).queue();
            privateChannel.sendMessage(BotManager.getMSG().get("joinEventDescription") + event.getDescription()).queue();
            privateChannel.sendMessage(BotManager.getMSG().get("joinEventDate").toString().replaceAll("<START_DATE>", DateMileKat.getDate(event.getStartDate())).replaceAll("<END_DATE>", DateMileKat.getDate(event.getEndDate()))).queue();
            // TODO: 29/06/2021 Send messages for event features (Ex. Team feature, tell to user how to create and join a team for this event)
            /*
            if (event.getFeatures().contains(TEAM)) {

            }
            */
        });
    }
}
