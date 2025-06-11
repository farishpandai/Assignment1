import java.time.LocalDate;

/**
 * Abstract base class for all task types.
 * It defines the common properties and behaviors that all tasks share.
 */
public abstract class Task {

    // Common properties for all tasks
    protected String taskName;
    protected String category;
    protected LocalDate dueDate;
    protected String priority;
    protected boolean done;

  
    public Task(String taskName, String category, LocalDate dueDate, String priority, boolean done) {
        this.taskName = taskName;
        this.category = category;
        this.dueDate = dueDate;
        this.priority = priority;
        this.done = done;
    }

    // Abstract method to be implemented by subclasses
    /**
     * Returns a string representation of the task's type, used for display purposes.
     * @return A formatted string for the task type (e.g., "[WORK]").
     */
    public abstract String getDisplayType();

    // Getters for all the variables
    public String getTaskName() {
        return this.taskName;
    }

    public String getCategory() {
        return this.category;
    }

    public String getPriority() {
        return this.priority;
    }

    public LocalDate getDueDate() {
        return this.dueDate;
    }

    public boolean isDone() {
        return done;
    }

    // Setters for all the variables
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    /**
     * Overridden toString method to provide a default formatted string for any task.
     */
    @Override
    public String toString() {
        return ("This is your current task:\nName\t\tCategory\t\tPriority\t\tDue Date\n" +
                getTaskName() + "\t\t" + getCategory() + "\t\t\t" + getPriority() + "\t\t\t" + getDueDate());
    }
}
