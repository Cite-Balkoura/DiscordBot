package fr.milekat.discordbot.bot.master.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.mapping.experimental.MorphiaReference;
import org.bson.types.ObjectId;

@Entity(value = "stepInput")
public class StepInput {
    @Id
    private ObjectId id;
    private MorphiaReference<Step> step;
    private String answer;

    public StepInput() {}

    public StepInput(Step step, String answer) {
        this.step = MorphiaReference.wrap(step);
        this.answer = answer;
    }

    public Step getStep() {
        return step.get();
    }

    public String getAnswer() {
        return answer;
    }
}
