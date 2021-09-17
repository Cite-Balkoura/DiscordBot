package fr.milekat.discordbot.bot.master.Moderation.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.mapping.experimental.MorphiaReference;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import org.bson.types.ObjectId;

import java.util.Date;

@Entity(value = "mute")
public class Mute {
    @Id
    private ObjectId id;
    private MorphiaReference<Profile> profile;
    private Date muteDate;
    private Date lastUpdate;
    private Date pardonDate;
    private String reasonMute;
    private String reasonPardon;
    private Boolean acknowledge;

    public Mute() {}

    public Mute(Profile profile, Date muteDate, Date pardonDate, String reasonMute) {
        this.profile = MorphiaReference.wrap(profile);
        this.muteDate = muteDate;
        this.pardonDate = pardonDate;
        this.reasonMute = reasonMute;
        this.lastUpdate = new Date();
        this.acknowledge = false;
    }

    public Profile getProfile() {
        return profile.get();
    }

    public Date getMuteDate() {
        return muteDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public Date getPardonDate() {
        return pardonDate;
    }

    public Mute setPardonDate(Date pardonDate) {
        this.pardonDate = pardonDate;
        return this;
    }

    public String getReasonMute() {
        return reasonMute;
    }

    public String getReasonPardon() {
        return reasonPardon;
    }

    public Mute setReasonPardon(String reasonPardon) {
        this.lastUpdate = new Date();
        this.reasonPardon = reasonPardon;
        return this;
    }

    public Mute setAcknowledge(Boolean acknowledge) {
        this.lastUpdate = new Date();
        this.acknowledge = acknowledge;
        return this;
    }
}
