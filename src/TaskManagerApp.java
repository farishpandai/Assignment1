import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class TaskManagerApp extends Application {
    //uses gridpane to make a table
    private GridPane taskGrid = new GridPane();

    private int currentRow = 1; // start at row 1 to leave room for headers

    public TaskValidator taskValidator = new TaskValidator();
    
   

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        //For title of the app
        Label header = new Label("Task Manager");   
        header.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 32)); 
        header.setTextFill(Color.TAN);  
        header.setMaxWidth(Double.MAX_VALUE);  
        header.setAlignment(Pos.CENTER);    

        //app FOnts
        Font mainLabelFont = Font.font("Book Antiqua", FontWeight.BOLD, 28);
        Font subLabelFont = Font.font("Book Antiqua", 25);
        
        // Input fields
        TextField taskName = new TextField();
        TextField category = new TextField();
        TextField dueDate = new TextField(); // Format: yyyy-MM-dd
        TextField priority = new TextField();

        taskName.setPromptText("Task Name");
        category.setPromptText("Category");
        dueDate.setPromptText("Due Date (YYYY-MM-DD)");
        priority.setPromptText("Priority (High/Medium/Low)");
        HBox texts = new HBox(taskName,category,dueDate,priority);



        // Add headers to taskGrid
        taskGrid.setHgap(10);
        taskGrid.setVgap(10);
        Label task = new Label("Task Name");
        task.setFont(mainLabelFont);
        Label cate = new Label("Category");
        cate.setFont(mainLabelFont);
        Label due = new Label("Due Date");
        due.setFont(mainLabelFont);
        Label prio = new Label("Priority");
        prio.setFont(mainLabelFont);
        taskGrid.add(task, 0, 0);
        taskGrid.add(cate, 1, 0);
        taskGrid.add(due, 2, 0);
        taskGrid.add(prio, 3, 0);
        
        taskGrid.setGridLinesVisible(true);
        taskGrid.setAlignment(Pos.CENTER);


        // Add Task button
        Button submitButton = new Button("Add Task");
        //Event on click for task
        submitButton.setOnAction(e -> {

                LocalDate date = taskValidator.dateValidate(dueDate.getText());
                Label name = new Label(taskName.getText());
                name.setFont(subLabelFont);
                Label cat = new Label(category.getText());
                cat.setFont(subLabelFont);
                Label date1 = new Label(date.toString());
                date1.setFont(subLabelFont);
                Label pri = new Label(priority.getText());
                pri.setFont(subLabelFont);
                Task newTask = new Task(taskName.getText(), category.getText(), date ,priority.getText());
                
                    if (taskValidator.validateTask(newTask)==true) {
                    taskGrid.add(name, 0, currentRow);
                    taskGrid.add(cat, 1, currentRow);
                    taskGrid.add(date1, 2, currentRow);
                    taskGrid.add(pri, 3, currentRow);
                    currentRow++;
                    taskValidator.showSuccessMessage("Task successfully added!");
                    taskName.clear();
                    category.clear();
                    dueDate.clear();
                    priority.clear();
                }
        });


        //Add exit and event on click for exit
        Button exitButton = new Button("Exit");
        exitButton.setPrefWidth(100);
        HBox exitBox = new HBox(exitButton);
        exitBox.setAlignment(Pos.CENTER);

        
        exitButton.setOnAction(e -> {
            taskValidator.confirmExit();
        });


        // Layout setup
        HBox inputBox = new HBox(10, texts, submitButton);
        inputBox.setAlignment(Pos.CENTER);
        VBox root = new VBox(20, header,inputBox, taskGrid,exitBox);
        root.setStyle("-fx-background-color: #FAF0E6;");
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Task Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
