package fr.milekat.discordbot.bot.master.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.UUID;

@Entity(value = "mute")
public class Mute {
    @Id
    private ObjectId id;
    @Indexed
    private UUID uuid;
    private Date muteDate;
    private Date pardonDate;
    private String reasonMute;
    private String reasonPardon;

    public Mute() {}

    public Mute(UUID uuid, Date muteDate, Date pardonDate, String reasonMute, String reasonPardon) {
        this.uuid = uuid;
        this.muteDate = muteDate;
        this.pardonDate = pardonDate;
        this.reasonMute = reasonMute;
        this.reasonPardon = reasonPardon;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Date getMuteDate() {
        return muteDate;
    }

    public Date getPardonDate() {
        return pardonDate;
    }

    public String getReasonMute() {
        return reasonMute;
    }

    public String getReasonPardon() {
        return reasonPardon;
    }
}
