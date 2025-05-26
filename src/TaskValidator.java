import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class TaskValidator {
    private Alert msg;

    public TaskValidator() {
        msg = new Alert(AlertType.NONE);
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

    //Need to be added as without this if the date text box is empty the program simply wont work
    public LocalDate dateValidate(String dateStr)
    {
        LocalDate date = LocalDate.parse("2000-02-02");
        try{
        date = LocalDate.parse(dateStr);
        }
        catch(DateTimeParseException e)
        {
             showErrorMessage("Invalid date format. Please use YYYY-MM-DD.");
        }
        return date;

    }

    //pop up showing successfull task added
     public void showSuccessMessage(String message) {
        
      
        msg.setAlertType(Alert.AlertType.INFORMATION);  
        msg.setTitle("Success Message"); 
        msg.setContentText(message); 
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
