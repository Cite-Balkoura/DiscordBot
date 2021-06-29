package fr.milekat.DiscordBot.bot.master.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Indexed;

import java.util.Date;
import java.util.UUID;

@Entity(value = "mute")
public class Mute {
    @Indexed
    private final UUID uuid;
    private final Date muteDate;
    private final Date pardonDate;
    private final String reasonMute;
    private final String reasonPardon;

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
