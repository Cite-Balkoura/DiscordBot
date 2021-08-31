package fr.milekat.discordbot.bot.master.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.mapping.experimental.MorphiaReference;
import fr.milekat.discordbot.Main;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bson.types.ObjectId;

import java.util.Date;

@Entity(value = "ban")
public class Ban {
    @Id
    private ObjectId id;
    private MorphiaReference<Profile> profile;
    private Long channelId;
    private Date banDate;
    private Date lastUpdate;
    private Date pardonDate;
    private String reasonBan;
    private String reasonPardon;

    public Ban() {}

    public Ban(Profile profile, Date banDate, Date pardonDate, String reasonBan) {
        this.profile = MorphiaReference.wrap(profile);
        this.banDate = banDate;
        this.pardonDate = pardonDate;
        this.reasonBan = reasonBan;
    }

    public Ban(Profile profile, Date banDate, Date pardonDate, String reasonBan, TextChannel textChannel) {
        this.profile = MorphiaReference.wrap(profile);
        this.banDate = banDate;
        this.pardonDate = pardonDate;
        this.reasonBan = reasonBan;
        this.channelId = textChannel.getIdLong();
    }

    public Profile getProfile() {
        return profile.get();
    }

    public TextChannel getChannel() {
        return Main.getJDA().getTextChannelById(channelId);
    }

    public Ban setChannel(TextChannel channel) {
        this.lastUpdate = new Date();
        this.channelId = channel.getIdLong();
        return this;
    }

    public Date getBanDate() {
        return banDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public Date getPardonDate() {
        return pardonDate;
    }

    public Ban setPardonDate(Date pardonDate) {
        this.lastUpdate = new Date();
        this.pardonDate = pardonDate;
        return this;
    }

    public String getReasonBan() {
        return reasonBan;
    }

    public String getReasonPardon() {
        return reasonPardon;
    }
}
