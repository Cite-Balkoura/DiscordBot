package fr.milekat.discordbot.bot.master.classes;

import dev.morphia.annotations.Entity;

@Entity(value = "stepInput")
public class StepInput {
    private final String name;
    private final String answer;

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
