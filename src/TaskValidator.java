/**
 *
 * @author LENOVO
 */
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.ArrayList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class TaskValidator {
    private Alert msg;
    private ArrayList<Task> taskList;

    public TaskValidator(ArrayList<Task> taskList) {
        msg = new Alert(AlertType.NONE);
        this.taskList = taskList;

    }
    
    //Show error message involved
    public void showErrorMessage(String errMessage)
    {
        msg.setAlertType(AlertType.ERROR);
        msg.setTitle("Invalid Input");
        msg.setContentText(errMessage);
        msg.show();
    }

    //Confirm with the user if they want to exit
    public void confirmExit()
    {
        msg.setAlertType(AlertType.CONFIRMATION);
        msg.setTitle("Confirmation Dialog");
        msg.setContentText("Are you sure to exit?");
        Optional<ButtonType> result = msg.showAndWait();
         if (result.get() == ButtonType.OK) {
            System.exit(0);
        }


    }

    //Pop up showing successful task added
     public void showSuccessMessage(String message) {
        
      
        msg.setAlertType(Alert.AlertType.INFORMATION);  
        msg.setTitle("Success Message"); 
        msg.setContentText(message); 
        msg.show(); 
    }
     
    
    public boolean validateTask(Task task, int ignoreIndex)
    {
        try
        {
            if (task.getTaskName().isEmpty()) 
            {
                throw new IllegalArgumentException("Task cannot be empty");
            }
            if (isDuplicateTask(task.getTaskName(), ignoreIndex)) {
                return false;
            }
            if (task.getCategory().isEmpty()) 
            {
                throw new IllegalArgumentException("You must choose the category");
            } 
            if (task.getDueDate()==null)
                throw new IllegalArgumentException("You must choose the due date");
            
            if (task.getDueDate().isBefore(LocalDate.now())) 
            {
                throw new IllegalArgumentException("The Date cannot be before the current date");
            } 
            if (task.getPriority().isEmpty()) 
            {
                throw new IllegalArgumentException("You must choose the priority");
            }
            else {
                return true;
            }
        }
        catch(IllegalArgumentException e)
        {
            showErrorMessage(e.getMessage());
            return false;
        }
    }
    
    //Pop up to confirm deletion
    public boolean confirmDelete()
    {
        msg.setAlertType(AlertType.CONFIRMATION);
        msg.setTitle("Confirmation Dialog");
        msg.setContentText("Are you sure you want to delete?");
        Optional<ButtonType> result = msg.showAndWait();
         if(result.isPresent() && result.get() == ButtonType.OK) {
           return true;
        }else{
            return false;
         }
    }
    
    //Set the value of the taskList
    public void setTaskList(ArrayList<Task> taskList) {
        this.taskList = taskList;
    }
    
      public boolean isDuplicateTask(String taskName, int ignoreIndex) {
          for (int i = 0; i < taskList.size(); i++) {
              if (i == ignoreIndex) continue;
              Task task = taskList.get(i);
              if (task.getTaskName().equalsIgnoreCase(taskName.trim())) {
                  showErrorMessage("A task with this name already exists");
                  return true;
              }
          }
          return false;
      }

  
    
}