package fr.milekat.discordbot.bot.events.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import dev.morphia.mapping.experimental.MorphiaReference;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.events.managers.EventManager;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import fr.milekat.discordbot.bot.master.core.managers.ProfileManager;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.UUID;

@Entity("team")
public class Team {
    @Id
    private ObjectId id;
    @Indexed(options = @IndexOptions(unique = true, sparse = true))
    private String teamName;
    private String eventName;
    @Indexed(options = @IndexOptions(unique = true, sparse = true))
    private MorphiaReference<Profile> owner;
    @Indexed(options = @IndexOptions(unique = true, sparse = true))
    private ArrayList<UUID> members;
    private boolean open;
    private long messageId;
    private long channelId;

    public Team() {}

    public Team(String eventName, String teamName, Profile owner) {
        this.eventName = eventName;
        this.teamName = teamName;
        this.owner = MorphiaReference.wrap(owner);
        this.open = true;
    }

    public ObjectId getId() {
        return id;
    }

    public Event getEvent() {
        return EventManager.getEvent(eventName);
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Profile getOwner() {
        return owner.get();
    }

    public ArrayList<Profile> getMembers() {
        if (members==null) return new ArrayList<>();
        return ProfileManager.getProfiles(members);
    }

    public void setMembers(ArrayList<Profile> members) {
        this.members = new ArrayList<>(members.stream().map(Profile::getUuid).toList());
    }

    public void addMember(Profile profile) {
        ArrayList<Profile> members = getMembers();
        members.add(profile);
        setMembers(members);
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public TextChannel getChannel() {
        return Main.getJDA().getTextChannelById(channelId);
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }
}
