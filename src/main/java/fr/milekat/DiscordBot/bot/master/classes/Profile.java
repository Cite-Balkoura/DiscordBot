package fr.milekat.DiscordBot.bot.master.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import fr.milekat.DiscordBot.bot.events.classes.Participation;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Entity(value = "profile")
public class Profile {
    @Indexed(options = @IndexOptions(unique = true))
    private final String name;
    @Indexed(options = @IndexOptions(unique = true))
    private final UUID uuid;
    @Indexed(options = @IndexOptions(unique = true))
    private final long discordId;
    private final Date registerDate;
    private final ArrayList<StepInput> registerForm;

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

    private final ArrayList<Participation> participating;
}
