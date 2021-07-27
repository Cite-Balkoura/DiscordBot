package fr.milekat.discordbot.bot.events.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;

@Entity(value = "eventMain")
public class Event {
    @Id
    private ObjectId id;
    @Indexed(options = @IndexOptions(unique = true))
    private String name;
    private String type;
    private ArrayList<EventFeature> eventFeatures;
    private Date startDate;
    private Date endDate;
    private String description;
    private long roleId;
    @Indexed(options = @IndexOptions(unique = true))
    private long categoryId;

    public enum EventFeature {
        TIME,
        OBJECTIVE,
        CITE,
        TEAM
    }

    public Event() {}

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

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }
}
