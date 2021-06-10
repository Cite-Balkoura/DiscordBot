package fr.milekat.DiscordBot.bot.events.classes;

import java.util.ArrayList;
import java.util.Date;

public class Event {
    private final String name;
    private final String type;
    private final ArrayList<EventFeature> features;
    private final Date startDate;
    private final Date endDate;
    private final String description;

    public Event(String name, String type, ArrayList<EventFeature> features, Date startDate, Date endDate, String description) {
        this.name = name;
        this.type = type;
        this.features = features;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
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
}
