package fr.milekat.discordbot.bot.events.features;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.events.classes.Event;
import fr.milekat.discordbot.bot.events.classes.Participation;
import fr.milekat.discordbot.bot.events.managers.EventManager;
import fr.milekat.discordbot.bot.events.managers.ParticipationManager;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import fr.milekat.discordbot.bot.master.core.managers.ProfileManager;
import fr.milekat.utils.DateMileKat;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.Optional;

public class JoinEvent extends ListenerAdapter {
    private final String JOIN_EVENT = "Join Event";

    /**
     * When a member of guild click on "Join Event" from a message in Event channel (to join an event)
     */
    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (!event.getChannel().equals(BotUtils.getChannel("cEvent"))) return;
        if (event.getButton()==null || !event.getButton().getLabel().equalsIgnoreCase(JOIN_EVENT)) return;
        registerPlayerToEvent(event.getMember(), event.getUser(), event.getMessage());
        event.reply("Done !").setEphemeral(true).queue();
    }

    /**
     * Add button on new message with embed in Event channel
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (Main.getJDA().getSelfUser().equals(event.getAuthor()) ||
                !event.getChannel().equals(BotUtils.getChannel("cEvent"))) return;
        if (event.getMessage().getEmbeds().isEmpty() || event.getMessage().getEmbeds().get(0).getTitle()==null) return;
        event.getChannel().sendMessageEmbeds(event.getMessage().getEmbeds().get(0))
                .setActionRow(Button.primary(event.getMessageId(), JOIN_EVENT)).queue();
        event.getMessage().delete().queue();
    }

    /**
     * Register the player to the event (If member is not banned)
     */
    private void registerPlayerToEvent(Member member, User user, Message message) {
        if (user.isBot() || message.getEmbeds().isEmpty()) return;
        //  Prevent a banned player to register to an event if he is currently banned
        if (member.getRoles().contains(BotUtils.getRole("rBan"))) return;
        Event event = EventManager.getEvent(message.getEmbeds().get(0).getTitle());
        Role role = BotUtils.getGuild().getRoleById(event.getRoleId());
        if (role==null) {
            Main.log("[ERROR] No role found for event: " + event.getName());
            return;
        }
        //  Check if member is already on this Event
        if (member.getRoles().contains(role)) return;
        BotUtils.getGuild().addRoleToMember(member, role).queue();
        Profile profile = ProfileManager.getProfile(user.getIdLong());
        if (profile == null) {
            Main.log("[ERROR] Can't find member profile in Event channel.");
            return;
        }
        //  Save his participation to the event
        ParticipationManager.create(new Participation(profile, event));
        Category category = BotUtils.getGuild().getCategoryById(event.getCategoryId());
        if (category==null) {
            Main.log("[Error] Event or Category of event " + event.getName() + " not found.");
            return;
        }
        Optional<TextChannel> channel = category.getTextChannels().stream().filter(textChannel ->
                textChannel.getName().equalsIgnoreCase(BotUtils.getMsg("teamBuilder.teamChannelName"))).findFirst();
        //  Send to user join event confirm & infos
        member.getUser().openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendMessage(BotUtils.getMsg("joinEvent.greeting")
                    .replaceAll("<EVENT_NAME>", event.getName())).queue();
            privateChannel.sendMessage(BotUtils.getMsg("joinEvent.description")
                    .replaceAll("<EVENT_DESC>", event.getDescription())).queue();
            privateChannel.sendMessage(BotUtils.getMsg("joinEvent.startDate")
                    .replaceAll("<START_DATE>", DateMileKat.getDate(event.getStartDate()))
                    .replaceAll("<END_DATE>", DateMileKat.getDate(event.getEndDate()))).queue();
            if (event.getEventFeatures().contains(Event.EventFeature.TEAM) && channel.isPresent())
                privateChannel.sendMessage(BotUtils.getMsg("joinEvent.hasTeam")
                    .replaceAll("<CHANNEL>", channel.get().getAsMention())).queue();
        });
    }
}
