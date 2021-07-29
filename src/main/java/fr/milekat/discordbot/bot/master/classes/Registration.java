package fr.milekat.discordbot.bot.master.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import dev.morphia.mapping.experimental.MorphiaReference;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.master.managers.StepInputManager;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.UUID;

@Entity(value = "registration")
public class Registration {
    @Id
    private ObjectId id;
    @Indexed(options = @IndexOptions(unique = true, sparse = true))
    private String username;
    @Indexed(options = @IndexOptions(unique = true, sparse = true))
    private UUID uuid;
    @Indexed(options = @IndexOptions(unique = true, sparse = true))
    private long discordId;
    private long registerChannelId;
    private String step;
    private MorphiaReference<ArrayList<StepInput>> inputs;

    public Registration() {}

    public Registration(long discordId, long registerChannelId) {
        this.discordId = discordId;
        this.registerChannelId = registerChannelId;
        this.step = "Start";
    }

    public Registration(String username, UUID uuid, long discordId, long registerChannelId, String step, ArrayList<StepInput> inputs) {
        this.username = username;
        this.uuid = uuid;
        this.discordId = discordId;
        this.registerChannelId = registerChannelId;
        this.step = step;
        this.inputs = MorphiaReference.wrap(inputs);
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
        return this.inputs==null ? new ArrayList<>() : new ArrayList<>(this.inputs.get());
    }

    public void setInputs(ArrayList<StepInput> inputs) {
        this.inputs = MorphiaReference.wrap(inputs);
    }

    public void addInputs(StepInput input) {
        ArrayList<StepInput> inputs = getInputs();
        ArrayList<StepInput> toRemove = new ArrayList<>();
        inputs.forEach(inputLoop -> {if (inputLoop.getStep().getName().equalsIgnoreCase(input.getStep().getName())) toRemove.add(inputLoop);});
        inputs.removeAll(toRemove);
        inputs.add(input);
        this.inputs = MorphiaReference.wrap(inputs);
        StepInputManager.save(input);
    }
}
