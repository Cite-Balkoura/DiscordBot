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
import java.util.HashMap;
import java.util.Optional;
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
    private long formId;
    private HashMap<Long, Boolean> votes;

    public Registration() {}

    public Registration(long discordId, long registerChannelId) {
        this.discordId = discordId;
        this.registerChannelId = registerChannelId;
        this.step = "Start";
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
        return new ArrayList<>(Optional.ofNullable(this.inputs.get()).orElse(new ArrayList<>()));

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

    public HashMap<Long, Boolean> getVotes() {
        return new HashMap<>(Optional.ofNullable(this.votes).orElse(new HashMap<>()));
    }

    public void addVote(Long userId, boolean accept) {
        HashMap<Long, Boolean> votes = getVotes();
        votes.put(userId, accept);
        this.votes = votes;
    }

    public long getFormId() {
        return formId;
    }

    public void setFormId(long formId) {
        this.formId = formId;
    }
}
