package fr.milekat.DiscordBot.bot.master.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;

import java.util.ArrayList;

@Entity(value = "step")
public class Step {
    @Indexed(options = @IndexOptions(unique = true))
    private final String name;
    private final String type;
    private final String message;
    private final int max;
    private final int min;
    private final String yes;
    private final String no;
    private final String returnStep;
    private final ArrayList<String> choices;
    private final String next;
    private final boolean save;

    public Step(String name, String type, String message, int max, int min, String yes, String no, String returnStep, ArrayList<String> choices, String next, boolean save) {
        this.name = name;
        this.type = type;
        this.message = message;
        this.max = max;
        this.min = min;
        this.yes = yes;
        this.no = no;
        this.returnStep = returnStep;
        this.choices = choices;
        this.next = next;
        this.save = save;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public String getYes() {
        return yes;
    }

    public String getNo() {
        return no;
    }

    public String getReturnStep() {
        return returnStep;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    public String getNext() {
        return next;
    }

    public boolean isSave() {
        return save;
    }
}
