package fr.milekat.discordbot.bot.master.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;

import java.util.ArrayList;
import java.util.UUID;

@Entity(value = "registration")
public class Registration {
    @Indexed(options = @IndexOptions(unique = true))
    private String name;
    @Indexed(options = @IndexOptions(unique = true))
    private UUID uuid;
    @Indexed(options = @IndexOptions(unique = true))
    private long discordId;
    private ArrayList<StepInput> inputs;

    public Registration(String name, UUID uuid, long discordId, ArrayList<StepInput> inputs) {
        this.name = name;
        this.uuid = uuid;
        this.discordId = discordId;
        this.inputs = inputs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public long getDiscordId() {
        return discordId;
    }

    public void setDiscordId(long discordId) {
        this.discordId = discordId;
    }

    public ArrayList<StepInput> getInputs() {
        return inputs;
    }

    public void setInputs(ArrayList<StepInput> inputs) {
        this.inputs = inputs;
    }
}
