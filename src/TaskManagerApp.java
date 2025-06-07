import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

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
import javafx.stage.FileChooser;
import javafx.scene.control.TextInputDialog;

public class TaskManagerApp extends Application {
    //uses gridpane to make a table
    private GridPane taskGrid = new GridPane();

    private int currentRow = 1; // start at row 1 to leave room for headers

    public TaskValidator taskValidator = new TaskValidator();

    private boolean fileCheckState = false;

    
    
   

    public static void main(String[] args) {
        launch(args);
    }

     private Scene buildIntroScene(Button startButton, Button loadButton) {
    Label introLabel = new Label("Welcome to Task Manager!");
    introLabel.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 32));
    VBox introLayout = new VBox(30, introLabel, startButton, loadButton);
    introLayout.setAlignment(Pos.CENTER);
    return new Scene(introLayout, 800, 600);
}

    @Override
    public void start(Stage primaryStage) {
        TextReadAndWrite readWrite = new TextReadAndWrite();

        // --- INTRO SCENE ---
        Button newTasksButton = new Button("New Tasks");
        Button loadButton = new Button("Load");
        Scene introScene = buildIntroScene(newTasksButton, loadButton);

       

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
                    readWrite.writeToFile(newTask);
                    taskName.clear();
                    category.clear();
                    dueDate.clear();
                    priority.clear();
                }
        });


        //Add exit and event on click for exit
        Button exitButton = new Button("Exit");
        Button saveButton = new Button("Save");
        exitButton.setPrefWidth(100);
        saveButton.setPrefWidth(100);
        HBox exitBox = new HBox(500,saveButton,exitButton);
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

        Scene mainScene = scene;//main Task Manager scene

        //Switch Scenes
        newTasksButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("New Task File");
            dialog.setHeaderText("Create New Task File");
            dialog.setContentText("Enter a name for your new task file:");

            dialog.showAndWait().ifPresent(filename -> {
                if (!filename.trim().isEmpty()) {
                    boolean created = readWrite.checkForFile(filename.trim());
                    if (!created) {
                        // File created set writing
                        readWrite.setFile(new File(filename.trim() + ".txt"));
                        fileCheckState = true;
                        primaryStage.setScene(mainScene);
                    } else {
                        // File exists
                        readWrite.setFile(new File(filename.trim() + ".txt"));
                        fileCheckState = true;
                        primaryStage.setScene(mainScene);
                    }
                } else {
                  
                }
            });
        });
        

        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Task File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                readWrite.setFile(selectedFile); 
                fileCheckState = true;
                primaryStage.setScene(mainScene);
                // Load tasks here
                ArrayList<String[]> savedTasks = readWrite.readFromSave();
                if (savedTasks != null) {
                    for (String[] taskFields : savedTasks) {
                        if (taskFields.length >= 4) {
                            Label name = new Label(taskFields[0].trim());
                            name.setFont(subLabelFont);
                            Label cat = new Label(taskFields[1].trim());
                            cat.setFont(subLabelFont);
                            Label date1 = new Label(taskFields[2].trim());
                            date1.setFont(subLabelFont);
                            Label pri = new Label(taskFields[3].trim());
                            pri.setFont(subLabelFont);

                            taskGrid.add(name, 0, currentRow);
                            taskGrid.add(cat, 1, currentRow);
                            taskGrid.add(date1, 2, currentRow);
                            taskGrid.add(pri, 3, currentRow);
                            currentRow++;
                        }
                    }
                }
            }
        });

        //Show Intro Scene First
        primaryStage.setTitle("Task Manager");
        primaryStage.setScene(introScene);
        primaryStage.show();
        
       
    

        
    }

   
}
