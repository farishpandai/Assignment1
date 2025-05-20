import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class TaskManagerApp extends Application {

    private GridPane taskGrid = new GridPane();
    private int currentRow = 1; // Start at row 1 to leave room for headers

    public TaskValidator taskValidator = new TaskValidator();
   

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Input fields
        TextField taskName = new TextField();
        TextField category = new TextField();
        TextField dueDate = new TextField(); // Format: yyyy-MM-dd
        TextField priority = new TextField();

        taskName.setPromptText("Task Name");
        category.setPromptText("Category");
        dueDate.setPromptText("Due Date (YYYY-MM-DD)");
        priority.setPromptText("Priority (High/Medium/Low)");


        // Feedback label
        Label feedbackLabel = new Label();

        // Add headers to taskGrid
        taskGrid.setHgap(10);
        taskGrid.setVgap(10);
        taskGrid.add(new Label("Task Name"), 0, 0);
        taskGrid.add(new Label("Category"), 1, 0);
        taskGrid.add(new Label("Due Date"), 2, 0);
        taskGrid.add(new Label("Priority"), 3, 0);

        // Add Task button
        Button submitButton = new Button("Add Task");
        submitButton.setOnAction(e -> {
            String name = taskName.getText();
            String cat = category.getText();
            String dateStr = dueDate.getText();
            String pri = priority.getText();
            
                LocalDate date = LocalDate.parse(dateStr);
                Task newTask = new Task(name, cat, date, pri);
                
                    if (taskValidator.validateTask(newTask)==true) {
                    taskGrid.add(new Label(name), 0, currentRow);
                    taskGrid.add(new Label(cat), 1, currentRow);
                    taskGrid.add(new Label(date.toString()), 2, currentRow);
                    taskGrid.add(new Label(pri), 3, currentRow);
                    currentRow++;
                    feedbackLabel.setText("Task added successfully.");
                    // Clear input fields
                    taskName.clear();
                    category.clear();
                    dueDate.clear();
                    priority.clear();
                } else 
                {   
                }
           
        });

        


        // Layout setup
        HBox inputBox = new HBox(10, taskName, category, dueDate, priority, submitButton);
        VBox root = new VBox(10, inputBox, feedbackLabel, taskGrid);
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Task Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
