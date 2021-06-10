package fr.milekat.DiscordBot.bot.events.classes;

public class EventFeature {
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
