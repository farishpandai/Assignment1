import java.time.LocalDate;

/**
 * For personal errands and stuff.
 * another specific type of Task.
 */
public class PersonalTask extends Task {

    /**
     * Constructor for PersonalTask. just calls the parent constructor.
     */
    public PersonalTask(String taskName, String category, LocalDate dueDate, String priority, boolean done) {
        super(taskName, category, dueDate, priority, done);
    }

    /**
     * the display type for this task. just returns "[PERSONAL]".
     */
    @Override
    public String getDisplayType() {
        return "[PERSONAL]";
    }
}
