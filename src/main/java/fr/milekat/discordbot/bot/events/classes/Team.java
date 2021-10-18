package fr.milekat.discordbot.bot.events.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.events.managers.EventManager;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import fr.milekat.discordbot.bot.master.core.managers.ProfileManager;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.UUID;

@Entity(value = "team")
public class Team {
    @Id
    private ObjectId id;
    @Indexed(options = @IndexOptions(unique = true, sparse = true))
    private String teamName;
    private String description;
    private String eventName;
    @Indexed(options = @IndexOptions(unique = true, sparse = true))
    private UUID owner;
    @Indexed(options = @IndexOptions(unique = true, sparse = true))
    private ArrayList<UUID> members;
    private boolean access;
    private long messageId;
    private long channelId;

    public Team() {}

    public Team(String eventName, String teamName, Profile owner) {
        this.eventName = eventName;
        this.teamName = teamName;
        this.owner = owner.getUuid();
        addMember(owner);
        this.description = "";
        this.access = true;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getOwner() {
        return owner;
    }

    public Profile getOwnerProfile() {
        return ProfileManager.getProfile(owner);
    }

    public ArrayList<UUID> getMembers() {
        return members;
    }

    public ArrayList<Profile> getMembersProfiles() {
        if (members==null) return new ArrayList<>();
        return ProfileManager.getProfiles(members);
    }

    public void setMembers(ArrayList<Profile> members) {
        this.members = new ArrayList<>(members.stream().map(Profile::getUuid).toList());
    }

    public void addMember(Profile profile) {
        if (members==null) members = new ArrayList<>();
        members.add(profile.getUuid());
    }

    public int getSize() {
        return members.size();
    }

    public boolean isOpen() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    public TextChannel getChannel() {
        return Main.getJDA().getTextChannelById(channelId);
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }
}
