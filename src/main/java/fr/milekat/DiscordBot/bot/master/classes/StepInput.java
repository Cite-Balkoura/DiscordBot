package fr.milekat.DiscordBot.bot.master.classes;

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
