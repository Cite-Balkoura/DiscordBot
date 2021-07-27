package fr.milekat.discordbot.bot.master.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

@Entity(value = "stepInput")
public class StepInput {
    @Id
    private ObjectId id;
    private String name;
    private String answer;

    public StepInput() {}

    public StepInput(String name, String answer) {
        this.name = name;
        this.answer = answer;
    }

    public String getName() {
        return name;
    }

    public String getAnswer() {
        return answer;
    }
}
