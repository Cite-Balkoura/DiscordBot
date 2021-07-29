package fr.milekat.discordbot.bot.master.classes;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import org.bson.types.ObjectId;

import java.util.ArrayList;

@Entity(value = "step")
public class Step {
    @Id
    private ObjectId id;
    @Indexed(options = @IndexOptions(unique = true))
    private String name;
    private StepTypes type;
    private String question;
    private int min;
    private int max;
    private String yes;
    private String no;
    private String returnStep;
    private ArrayList<String> choices;
    private String next;
    private boolean save;

    public enum StepTypes{
        TEXT,
        VALID,
        CHOICES,
        FINAL
    }

    public Step() {}

    public Step(String name, StepTypes type, String question, int min, int max, String yes, String no, String returnStep, ArrayList<String> choices, String next, boolean save) {
        this.name = name;
        this.type = type;
        this.question = question;
        this.min = min;
        this.max = max;
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

    public StepTypes getType() {
        return type;
    }

    public String getQuestion() {
        return question;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
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
