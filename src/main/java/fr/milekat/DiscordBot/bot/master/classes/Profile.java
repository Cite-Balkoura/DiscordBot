package fr.milekat.DiscordBot.bot.master.classes;

import fr.milekat.DiscordBot.bot.events.classes.Participation;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Profile {
    private final String name;
    private final UUID uuid;
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
