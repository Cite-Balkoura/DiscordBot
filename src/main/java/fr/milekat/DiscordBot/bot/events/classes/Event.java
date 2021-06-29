package fr.milekat.DiscordBot.bot.events.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;

import java.util.ArrayList;
import java.util.Date;

@Entity(value = "event")
public class Event {
    @Indexed(options = @IndexOptions(unique = true))
    private final String name;
    private final String type;
    private final ArrayList<EventFeature> features;
    private final Date startDate;
    private final Date endDate;
    private final String description;
    private final long roleId;

    public Event(String name, String type, ArrayList<EventFeature> features, Date startDate, Date endDate, String description, long roleId) {
        this.name = name;
        this.type = type;
        this.features = features;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public ArrayList<EventFeature> getFeatures() {
        return features;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description;
    }

    public long getRoleId() {
        return roleId;
    }
}
