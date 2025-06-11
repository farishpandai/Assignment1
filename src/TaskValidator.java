import java.time.LocalDate;
import java.util.Optional;
import java.util.ArrayList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * This class handles all our input validation.
 * also handles all the pop-up messages (errors, success, confirmations).
 */
public class TaskValidator {
    private Alert msg; // the pop-up box object
    private ArrayList<Task> taskList;

    public TaskValidator(ArrayList<Task> taskList) {
        msg = new Alert(Alert.AlertType.NONE);
        this.taskList = taskList;
    }
    
    // shows a big red error pop-up.
    public void showErrorMessage(String errMessage) {
        msg.setAlertType(Alert.AlertType.ERROR);
        msg.setTitle("Invalid Input");
        msg.setContentText(errMessage);
        msg.show();
    }

    // "Are you sure you wanna exit?" pop-up.
    public void confirmExit() {
        msg.setAlertType(Alert.AlertType.CONFIRMATION);
        msg.setTitle("Confirmation Dialog");
        msg.setContentText("Are you sure to exit?");
        Optional<ButtonType> result = msg.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    // shows "Success!" message.
    public void showSuccessMessage(String message) {
        msg.setAlertType(Alert.AlertType.INFORMATION);  
        msg.setTitle("Success Message"); 
        msg.setContentText(message); 
        msg.show(); 
    }
     
    /**
     * runs all the checks on a task before we add or update it.
     * this method calls the specific validation logic in the subclasses too .
     */
    public boolean validateTask(Task task, int ignoreIndex) {
        try {
            // -- General Checks for ALL task types ---
            if (task.getTaskName().isEmpty()) {
                throw new IllegalArgumentException("Task name cannot be empty.");
            }
            if (isDuplicateTask(task.getTaskName(), ignoreIndex)) {
                return false; // error message is shown inside the method
            }
            if (task.getCategory() == null || task.getCategory().isEmpty()) {
                throw new IllegalArgumentException("You must choose a category.");
            } 
            if (task.getDueDate() == null) {
                throw new IllegalArgumentException("You must choose a due date.");
            }
            if (task.getDueDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("The due date cannot be in the past.");
            } 
            if (task.getPriority() == null || task.getPriority().isEmpty()) {
                throw new IllegalArgumentException("You must choose a priority.");
            }
            
            // if the task is a worktask, run its special validation rules.
            if (task instanceof WorkTask) {
                ((WorkTask) task).validate();
            }

            
            return true; 
            
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage()); // catch any error and show it
            return false;
        }
    }
    
    // "Are you sure you wanna delete this?" pop-up.
    public boolean confirmDelete() {
        msg.setAlertType(Alert.AlertType.CONFIRMATION);
        msg.setTitle("Confirmation Dialog");
        msg.setContentText("Are you sure you want to delete?");
        Optional<ButtonType> result = msg.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    // just a setter to update the task list.
    public void setTaskList(ArrayList<Task> taskList) {
        this.taskList = taskList;
    }
    
    /**
     * Checks if a task with the same name already exists.
     */
    public boolean isDuplicateTask(String taskName, int ignoreIndex) {
        for (int i = 0; i < taskList.size(); i++) {
            if (i == ignoreIndex) continue; // skip the task we're currently editing
            Task task = taskList.get(i);
            if (task.getTaskName().equalsIgnoreCase(taskName.trim())) {
                showErrorMessage("A task with this name already exists.");
                return true;
            }
        }
        return false;
    }
}
