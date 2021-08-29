package fr.milekat.discordbot.bot.master.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.mapping.experimental.MorphiaReference;
import fr.milekat.discordbot.Main;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bson.types.ObjectId;

import java.util.Date;

@Entity(value = "mute")
public class Mute {
    @Id
    private ObjectId id;
    private MorphiaReference<Profile> profile;
    private Long channelId;
    private Date muteDate;
    private Date pardonDate;
    private String reasonMute;
    private String reasonPardon;

    public Mute() {}

    public Mute(Profile profile, Date muteDate, Date pardonDate, String reasonMute) {
        this.profile = MorphiaReference.wrap(profile);
        this.muteDate = muteDate;
        this.pardonDate = pardonDate;
        this.reasonMute = reasonMute;
    }

    public Profile getProfile() {
        return profile.get();
    }

    public TextChannel getChannel() {
        return Main.getJDA().getTextChannelById(channelId);
    }

    public Date getMuteDate() {
        return muteDate;
    }

    public Date getPardonDate() {
        return pardonDate;
    }

    public void setPardonDate(Date pardonDate) {
        this.pardonDate = pardonDate;
    }

    public String getReasonMute() {
        return reasonMute;
    }

    public String getReasonPardon() {
        return reasonPardon;
    }

    public void setReasonPardon(String reasonPardon) {
        this.reasonPardon = reasonPardon;
    }
}
