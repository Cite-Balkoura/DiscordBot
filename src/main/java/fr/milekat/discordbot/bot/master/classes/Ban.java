package fr.milekat.discordbot.bot.master.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.UUID;

@Entity(value = "ban")
public class Ban {
    @Id
    private ObjectId id;
    @Indexed
    private UUID uuid;
    private Date banDate;
    private Date pardonDate;
    private String reasonBan;
    private String reasonPardon;

    public Ban() {}

    public Ban(UUID uuid, Date banDate, Date pardonDate, String reasonBan, String reasonPardon) {
        this.uuid = uuid;
        this.banDate = banDate;
        this.pardonDate = pardonDate;
        this.reasonBan = reasonBan;
        this.reasonPardon = reasonPardon;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Date getBanDate() {
        return banDate;
    }

    public Date getPardonDate() {
        return pardonDate;
    }

    public String getReasonBan() {
        return reasonBan;
    }

    public String getReasonPardon() {
        return reasonPardon;
    }
}
