import java.time.LocalDate;

/**
 * For simple reminders.
 */
public class ReminderTask extends Task {

    /**
     * simplified constructor for reminders.
     */
   public ReminderTask(String taskName, LocalDate dueDate, String priority, boolean done) {
    // just call the main constructor.
    super(taskName, "Reminder", dueDate, priority, done);
}

    /**
     * the display type for this task.
     * @return just returns "[REMINDER]".
     */
    @Override
    public String getDisplayType() {
        return "[REMINDER]";
    }
}
