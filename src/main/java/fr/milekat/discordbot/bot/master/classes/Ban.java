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
    private Date pardonDate;
    private String reasonBan;
    private String reasonPardon;

    public Ban() {}

    public Ban(Profile profile, Date banDate, String reasonBan, Date pardonDate) {
        this.profile = MorphiaReference.wrap(profile);
        this.banDate = banDate;
        this.reasonBan = reasonBan;
        this.pardonDate = pardonDate;
    }

    public Profile getProfile() {
        return profile.get();
    }

    public TextChannel getChannel() {
        return Main.getJDA().getTextChannelById(channelId);
    }

    public Date getBanDate() {
        return banDate;
    }

    public Date getPardonDate() {
        return pardonDate;
    }

    public void setPardonDate(Date pardonDate) {
        this.pardonDate = pardonDate;
    }

    public String getReasonBan() {
        return reasonBan;
    }

    public String getReasonPardon() {
        return reasonPardon;
    }

    public void setReasonPardon(String reasonPardon) {
        this.reasonPardon = reasonPardon;
    }
}
