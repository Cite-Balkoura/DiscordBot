package fr.milekat.discordbot.bot.events.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.mapping.experimental.MorphiaReference;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import org.bson.types.ObjectId;

@Entity(value = "participation")
public class Participation {
    @Id
    private ObjectId id;
    private MorphiaReference<Profile> profile;
    private MorphiaReference<Event> event;

    public Participation() {}

    public Participation(Profile profile, Event event) {
        this.profile = MorphiaReference.wrap(profile);
        this.event = MorphiaReference.wrap(event);
    }

    public Profile getProfile() {
        return profile.get();
    }

    public Event getEvent() {
        return event.get();
    }
}
