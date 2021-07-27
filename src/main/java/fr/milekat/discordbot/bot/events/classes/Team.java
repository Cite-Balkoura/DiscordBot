package fr.milekat.discordbot.bot.events.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import dev.morphia.mapping.experimental.MorphiaReference;
import fr.milekat.discordbot.bot.master.classes.Profile;
import org.bson.types.ObjectId;

import java.util.ArrayList;

@Entity("eventTeam")
public class Team {
    @Id
    private ObjectId id;
    private String name;
    private MorphiaReference<Event> event;
    @Indexed(options = @IndexOptions(unique = true))
    private MorphiaReference<ArrayList<Profile>> members;

    public Team() {}

    public Team(Event event, String name) {
        this.event = MorphiaReference.wrap(event);
        this.name = name;
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

    public ArrayList<Profile> getMembers() {
        return members.get();
    }

    public void setMembers(ArrayList<Profile> members) {
        this.members = MorphiaReference.wrap(members);
    }
}
