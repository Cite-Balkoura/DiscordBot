package fr.milekat.discordbot.bot.events.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import dev.morphia.mapping.experimental.MorphiaReference;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import org.bson.types.ObjectId;

import java.util.ArrayList;

@Entity("eventTeam")
public class Team {
    @Id
    private ObjectId id;
    @Indexed(options = @IndexOptions(unique = true, sparse = true))
    private String name;
    private MorphiaReference<Event> event;
    @Indexed(options = @IndexOptions(unique = true, sparse = true))
    private MorphiaReference<Profile> owner;
    @Indexed(options = @IndexOptions(unique = true, sparse = true))
    private MorphiaReference<ArrayList<Profile>> members;
    private boolean open;
    private long messageId;

    public Team() {}

    public Team(Event event, String name, Profile owner) {
        this.event = MorphiaReference.wrap(event);
        this.name = name;
        this.owner = MorphiaReference.wrap(owner);
        this.open = true;
    }

    public Event getEvent() {
        return event.get();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Profile getOwner() {
        return owner.get();
    }

    public ArrayList<Profile> getMembers() {
        return this.members==null ? new ArrayList<>() : new ArrayList<>(this.members.get());
    }

    public void setMembers(ArrayList<Profile> members) {
        this.members = MorphiaReference.wrap(members);
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

    public long getChannelId() {
        return Main.getJDA().getCategoryById(getEvent().getCategoryId()).getTextChannels().stream()
                .filter(textChannel -> getName().equalsIgnoreCase(BotUtils.getMsg("teamBuilder.teamChannelName")))
                .findFirst().get().getIdLong();
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }
}
