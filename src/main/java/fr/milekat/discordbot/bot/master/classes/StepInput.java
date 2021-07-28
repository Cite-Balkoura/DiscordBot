package fr.milekat.discordbot.bot.master.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

@Entity(value = "stepInput")
public class StepInput {
    @Id
    private ObjectId id;
    private Step step;
    private String answer;

    public StepInput() {}

    public StepInput(Step step, String answer) {
        this.step = step;
        this.answer = answer;
    }

    public Step getStep() {
        return step;
    }

    public String getAnswer() {
        return answer;
    }
}
