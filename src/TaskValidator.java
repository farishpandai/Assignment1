import java.time.LocalDate;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class TaskValidator {
    private Alert msg;

    public TaskValidator() {
        msg = new Alert(AlertType.NONE);
    }
    

    public void showErrorMessage(String errMessage)
    {
        msg.setAlertType(AlertType.ERROR);
        msg.setTitle("Invalid Input");
        msg.setContentText(errMessage);
        msg.show();
    }


    public boolean validateTask(Task task)
    {
        
        
        try
        {
            //Checks for error and throws an exception
            if (task.getTaskName().isEmpty()) 
            {
                throw new IllegalArgumentException("Task cannot be empty");
            }
            else if (task.getCategory().isEmpty()) 
            {
                throw new IllegalArgumentException("Category cannot be empty");
            } 
            else if (task.getDueDate().isBefore(LocalDate.now())) 
            {
                throw new IllegalArgumentException("The Date cannot be before the current date");
            } 
            if ((!task.getPriority().equalsIgnoreCase("High"))&&!task.getPriority().equalsIgnoreCase("Medium")&&!task.getPriority().equalsIgnoreCase("Low"))
            {
                throw new IllegalArgumentException("Valid Priority values can only be High,Medium or Low");
            }
            else {

                return true;
                
            }
            
        }
        //Catches the exception and display the appropriate error
        catch(IllegalArgumentException e)
        {
            showErrorMessage(e.getMessage());
            return false;
        }
    }

  
    
}
