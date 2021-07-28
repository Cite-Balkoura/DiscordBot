package fr.milekat.discordbot.bot.events.features;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.events.classes.Event;
import fr.milekat.discordbot.bot.events.classes.Participation;
import fr.milekat.discordbot.bot.events.managers.EventManager;
import fr.milekat.discordbot.bot.events.managers.ParticipationManager;
import fr.milekat.discordbot.bot.master.classes.Profile;
import fr.milekat.discordbot.bot.master.managers.ProfileManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;

public class JoinEvent extends ListenerAdapter {
    private final String JOIN_EVENT = "Join Event";

    /**
     * When a member of guild click on "Join Event" from a message in Event channel (to join an event)
     */
    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (!event.getChannel().equals(BotUtils.getChannel("cEvent"))) return;
        if (event.getButton()==null || !event.getButton().getLabel().equalsIgnoreCase(JOIN_EVENT)) return;
        addPlayerEventReg(event.getMember(), event.getUser(), event.getMessage());
    }

    /**
     * Add button on new message in Event channel
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (!event.getChannel().equals(BotUtils.getChannel("cEvent"))) return;
        if (event.getMessage().getEmbeds().isEmpty() || event.getMessage().getEmbeds().get(0).getTitle()==null) return;
        event.getMessage().getButtons().add(Button.primary(event.getMessage().getEmbeds().get(0).getTitle(), JOIN_EVENT).withStyle(ButtonStyle.PRIMARY));
    }

    /**
     * Register the player to the event (If member is not banned)
     */
    private void addPlayerEventReg(Member member, User user, Message message) {
        if (user.isBot() || message.getEmbeds().isEmpty()) return;
        //  Prevent a banned player to register to an event if he is currently banned
        if (member.getRoles().contains(BotUtils.getRole("rBan"))) return;
        Event event = EventManager.getEvent(message.getEmbeds().get(0).getTitle());
        //  Check if member is already on this Event
        if (member.getRoles().contains(Main.getJDA().getRoleById(event.getRoleId()))) return;
        Profile profile = ProfileManager.getProfile(user.getIdLong());
        if (profile == null) {
            Main.log("[ERROR] Can't find member profile in Event channel.");
            return;
        }
        //  Save his participation to the event
        ParticipationManager.save(new Participation(profile.getUuid(), event));
        /*user.openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendMessage(BotManager.getMSG().get("joinEvent").toString().replaceAll("<EVENT_NAME>", event.getName())).queue();
            privateChannel.sendMessage(BotManager.getMSG().get("joinEventDescription") + event.getDescription()).queue();
            privateChannel.sendMessage(BotManager.getMSG().get("joinEventDate").toString().replaceAll("<START_DATE>", DateMileKat.getDate(event.getStartDate())).replaceAll("<END_DATE>", DateMileKat.getDate(event.getEndDate()))).queue();
            if (event.getFeatures().stream().anyMatch(eventFeature -> eventFeature.getName().equalsIgnoreCase("Team"))) {
            }
        });*/// TODO: 30/06/2021 Keep this things ?
    }
}
