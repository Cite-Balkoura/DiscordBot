package fr.milekat.discordbot.bot.events.classes;

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
    private final ArrayList<EventFeature> eventFeatures;
    private final Date startDate;
    private final Date endDate;
    private final String description;
    private final long roleId;
    private final long categoryId;

    public Event(String name, String type, ArrayList<EventFeature> eventFeatures, Date startDate, Date endDate, String description, long roleId, long categoryId) {
        this.name = name;
        this.type = type;
        this.eventFeatures = eventFeatures;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.roleId = roleId;
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public ArrayList<EventFeature> getEventFeatures() {
        return eventFeatures;
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

    public long getCategoryId() {
        return categoryId;
    }
}
