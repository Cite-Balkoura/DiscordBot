package fr.milekat.DiscordBot.bot.events.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Indexed;

@Entity(value = "eventFeature")
public class EventFeature {
    @Indexed
    private final String name;
    private final boolean classed;

    public EventFeature(String name, boolean classed) {
        this.name = name;
        this.classed = classed;
    }

    public String getName() {
        return name;
    }

    public boolean isClassed() {
        return classed;
    }
}
