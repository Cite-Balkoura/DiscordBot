package fr.milekat.discordbot.bot.master.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import fr.milekat.discordbot.Main;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.UUID;

@Entity(value = "registration")
public class Registration {
    @Id
    private ObjectId id;
    @Indexed(options = @IndexOptions(unique = true))
    private String username;
    @Indexed(options = @IndexOptions(unique = true))
    private UUID uuid;
    @Indexed(options = @IndexOptions(unique = true))
    private long discordId;
    private long registerChannelId;
    private String step;
    private ArrayList<StepInput> inputs;

    public Registration() {}

    public Registration(long discordId, long registerChannelId) {
        this.discordId = discordId;
        this.registerChannelId = registerChannelId;
        this.step = "START";
    }

    public Registration(String username, UUID uuid, long discordId, long registerChannelId, String step, ArrayList<StepInput> inputs) {
        this.username = username;
        this.uuid = uuid;
        this.discordId = discordId;
        this.registerChannelId = registerChannelId;
        this.step = step;
        this.inputs = inputs;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public TextChannel getChannel() {
        return Main.getJDA().getTextChannelById(registerChannelId);
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public ArrayList<StepInput> getInputs() {
        return inputs;
    }

    public void setInputs(ArrayList<StepInput> inputs) {
        this.inputs = inputs;
    }

    public void addInputs(StepInput input) {
        this.inputs.add(input);
    }
}
