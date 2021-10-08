package fr.milekat.discordbot.bot.events.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import fr.milekat.discordbot.Main;
import net.dv8tion.jda.api.entities.Category;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;

@Entity(value = "event")
public class Event {
    @Id
    private ObjectId id;
    @Indexed(options = @IndexOptions(unique = true))
    private String name;
    @Indexed(options = @IndexOptions(unique = true))
    private String database;
    private String type;
    private ArrayList<EventFeature> eventFeatures;
    private Date startDate;
    private Date endDate;
    private String description;
    @Indexed(options = @IndexOptions(unique = true))
    private long roleId;
    @Indexed(options = @IndexOptions(unique = true))
    private long categoryId;
    @Indexed(options = @IndexOptions(unique = true))
    private long categoryTeamId;

    public enum EventFeature {
        TIME,
        OBJECTIVE,
        CITE,
        TEAM
    }

    public Event() {}

    public String getName() {
        return name;
    }

    public String getDatabase() {
        return database;
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

    public long getCategoryTeamId() {
        return categoryTeamId;
    }

    public Category getCategoryTeam() {
        return Main.getJDA().getCategoryById(categoryTeamId);
    }
}
