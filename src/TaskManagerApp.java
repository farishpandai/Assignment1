import java.time.LocalDate;
public class TaskManagerApp extends App {


    public static void main(String[] args) {
        launch(args);
        //Uses the constructor to create Task with error and no error
        Task task1 = new Task();
        Task taskError = new Task("test","Science",LocalDate.of(2025, 1, 22),"High");
        Task taskNoError = new Task("test","Science",LocalDate.now().plusDays(5),"High");
        //Prints out the results
        System.out.println(taskError.toString());
        System.out.println("\n"+TaskValidator.validateTask(taskError)+"\n");
        System.out.println(taskNoError.toString());
        System.out.println("\n"+TaskValidator.validateTask(taskNoError));
        
    }
    
}
