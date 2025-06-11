import java.time.LocalDate;

/**
 * For all our work-related stuff like assignments, exams, etc.
 * it's a specific type of Task.
 */
public class WorkTask extends Task {

    /**
     * Constructor for WorkTask. just passes everything up to the main Task constructor.
     */
    public WorkTask(String taskName, String category, LocalDate dueDate, String priority, boolean done) {
        super(taskName, category, dueDate, priority, done);
    }

    /**
     * the display type for this task. just returns "[WORK]".
     */
    @Override
    public String getDisplayType() {
        return "[WORK]";
    }

    /**
     * special validation rules just for WorkTasks.
     * work tasks gotta be serious, so we have stricter rules.
     */
    public void validate() throws IllegalArgumentException {
        // can't have a low priority work task
        if ("Low".equalsIgnoreCase(super.getPriority())) {
            throw new IllegalArgumentException("Work tasks cannot have a low priority.");
        }
        // make sure the name isn't just one letter or something
        if (super.getTaskName().length() < 5) {
             throw new IllegalArgumentException("Work task name must be at least 5 characters.");
        }
    }
}