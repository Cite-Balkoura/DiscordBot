package fr.milekat.discordbot.bot.master.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import fr.milekat.discordbot.bot.events.classes.Participation;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Entity(value = "profile")
public class Profile {
    @Id
    private ObjectId id;
    @Indexed(options = @IndexOptions(unique = true))
    private String name;
    @Indexed(options = @IndexOptions(unique = true))
    private UUID uuid;
    @Indexed(options = @IndexOptions(unique = true))
    private long discordId;
    private Date registerDate;
    private ArrayList<StepInput> registerForm;
    private ArrayList<Participation> participating;

    public Profile() {}

    public Profile(String name, UUID uuid, long discordId, Date registerDate, ArrayList<StepInput> registerForm, ArrayList<Participation> participating) {
        this.name = name;
        this.uuid = uuid;
        this.discordId = discordId;
        this.registerDate = registerDate;
        this.registerForm = registerForm;
        this.participating = participating;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getDiscordId() {
        return discordId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public ArrayList<StepInput> getRegisterForm() {
        return registerForm;
    }

    public ArrayList<Participation> getParticipating() {
        return participating;
    }
}
