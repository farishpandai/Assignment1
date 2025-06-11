import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * main class for the Task Manager App.
 */
public class TaskManagerApp extends Application {
    private GridPane taskGrid = new GridPane();
    private int currentRow = 1; // start at row 1, cuz row 0 is for headers
    
    // this is our main list of tasks. It's gonna hold WorkTasks, PersonalTasks
    private final ArrayList<Task> currentTasks = new ArrayList<>();
    public TaskValidator taskValidator = new TaskValidator(currentTasks);
    
    // flag to track if we're editing an existing task or adding a new one
    private boolean isEditing = false;
    private int editingRow = -1; // -1 means we're not editing anything
    
    // the searcher object
    private TasksSearcher tasksSearcher = new TasksSearcher();
    
    // input fields for adding/editing tasks
    private ComboBox<String> choTaskPri = new ComboBox<>();
    private TextField taskName = new TextField();
    private DatePicker dueDatePicker = new DatePicker();

    // radio buttons for picking the task category
    private ToggleGroup categoryGroup = new ToggleGroup();
    private RadioButton workRadio = new RadioButton("Work (Assignment/Exam/etc.)");
    private RadioButton personalRadio = new RadioButton("Personal");
    private RadioButton reminderRadio = new RadioButton("Reminder");

    // main entry point
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * builds the first screen the user sees.
     * just a welcome message and buttons to start a new list or load one.
     */
    private Scene buildIntroScene(Stage primaryStage, TextReadAndWrite readWrite) {
        Label introLabel = new Label("Welcome to Task Manager!");
        introLabel.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 32));
        
        Button newTasksButton = new Button("New Task List");
        newTasksButton.setPrefWidth(150);
        newTasksButton.setOnAction(e -> handleNewTaskList(primaryStage, readWrite));

        Button loadButton = new Button("Load Task List");
        loadButton.setPrefWidth(150);
        loadButton.setOnAction(e -> handleLoadTaskList(primaryStage, readWrite));

        VBox introLayout = new VBox(30, introLabel, newTasksButton, loadButton);
        introLayout.setAlignment(Pos.CENTER);
        return new Scene(introLayout, 800, 600);
    }
    
    /**
     * figures out which radio button is selected.
     * return "Work", "Personal", or "Reminder" as a String.
     */
    private String getSelectedCategoryType() {
        if (workRadio.isSelected()) return "Work";
        if (personalRadio.isSelected()) return "Personal";
        if (reminderRadio.isSelected()) return "Reminder";
        return ""; // Return empty if nothing's selected
    }

    /**
     * Sets the radio button selection when editing a task.
     * the category string of the task being edited.
     */
    private void setSelectedCategory(String category) {
        categoryGroup.selectToggle(null); // Clear first
        if ("Reminder".equals(category)) {
             reminderRadio.setSelected(true);
        } else if ("Personal".equals(category)){
            personalRadio.setSelected(true);
        }
        else {
            workRadio.setSelected(true); // Default to work
        }
    }

    /**
     * This is the big one. Adds a single row to our task grid display.
     * Creates all the labels, buttons, and checkbox for one task.
     */
    private void addTaskRowToGrid(int row, Task task, TextReadAndWrite readWrite) {
        Font font = Font.font("Book Antiqua", 20);
        
        // Just the task name, no need for the [WORK] prefix stuff anymore, cleaner this way
        Label name = new Label(task.getTaskName());
        name.setFont(font);
        Label cat = new Label(task.getCategory());
        cat.setFont(font);
        Label date1 = new Label(task.getDueDate().toString());
        date1.setFont(font);
        Label pri = new Label(task.getPriority());
        pri.setFont(font);
        CheckBox doneCheck = new CheckBox();
        doneCheck.setSelected(task.isDone());

        // Add everything to the grid at the specified row
        taskGrid.add(name, 0, row);
        taskGrid.add(cat, 1, row);
        taskGrid.add(date1, 2, row);
        taskGrid.add(pri, 3, row);
        taskGrid.add(doneCheck, 4, row);

        Button editBtn = new Button("Edit");
        Button deleteBtn = new Button("Delete");
        taskGrid.add(editBtn, 5, row);
        taskGrid.add(deleteBtn, 6, row);

        final int taskIndex = row - 1; // Need this for the lambda expressions below

        // Event handler for the 'done' checkbox
        doneCheck.setOnAction(ev -> {
            if (taskIndex < currentTasks.size()) {
                currentTasks.get(taskIndex).setDone(doneCheck.isSelected());
                readWrite.saveAllTasks(currentTasks); // Save immediately
            }
        });

        // Event handler for the edit button
        editBtn.setOnAction(ev -> {
            // Populate the input fields with the task's data
            taskName.setText(task.getTaskName());
            setSelectedCategory(task.getCategory());
            dueDatePicker.setValue(task.getDueDate());
            choTaskPri.setValue(task.getPriority());
            
            // set the editing flags
            isEditing = true;
            editingRow = row;
            
            // Grey out the row being edited so it's obvious
            taskGrid.getChildren().forEach(node -> {
                 if (GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == row) {
                    node.setDisable(true);
                 }
            });
        });

        // Event handler for the delete button
        deleteBtn.setOnAction(ev -> {
            if (taskValidator.confirmDelete()) {
                if (taskIndex < currentTasks.size()) {
                    currentTasks.remove(taskIndex); // Remove from the list
                    updateGrid(readWrite); // redraw the whole grid
                    readWrite.saveAllTasks(currentTasks); // Save the changes
                }
            }
        });
    }

    /**
     * Refreshes the entire task grid.
     * Clears everything and then rebuilds it from the currentTasks list.
     */
    private void updateGrid(TextReadAndWrite readWrite) {
        taskGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
        currentRow = 1; // Reset row counter
        for (Task task : currentTasks) {
            addTaskRowToGrid(currentRow, task, readWrite);
            currentRow++;
        }
    }
    
    /**
     * Simple helper to clear all the input fields after adding/editing a task.
     */
     private void clearInputFields() {
        taskName.clear();
        categoryGroup.selectToggle(null);
        dueDatePicker.setValue(null);
        choTaskPri.setValue(null);
    }

    /**
     * Builds the main scene where all the action happens.
     */
    private Scene buildMainScene(Stage primaryStage, TextReadAndWrite readWrite) {
        Label header = new Label("Task Manager");
        header.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 32));
        header.setTextFill(Color.DARKSLATEBLUE); 
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        Font mainLabelFont = Font.font("Book Antiqua", FontWeight.BOLD, 28);

        // -- Search Bar Setup --
        TextField search = new TextField();
        search.setPromptText("Enter Search");
        Button searchButton = new Button("Search");
        searchButton.setPrefWidth(100);
        Button clearSearchButton = new Button("Clear");
        clearSearchButton.setPrefWidth(100);
        HBox searchBar = new HBox(10, search, searchButton, clearSearchButton);
        searchBar.setAlignment(Pos.CENTER);

        // -- Input Fields Setup ---
        workRadio.setToggleGroup(categoryGroup);
        personalRadio.setToggleGroup(categoryGroup);
        reminderRadio.setToggleGroup(categoryGroup);
        VBox categoryRadioBox = new VBox(5, workRadio, personalRadio, reminderRadio);

        choTaskPri.setPromptText("Priority");
        choTaskPri.getItems().addAll("High", "Medium", "Low");
        taskName.setPromptText("Task Name");
        dueDatePicker.setPromptText("Due Date");

        HBox texts = new HBox(10, taskName, categoryRadioBox, dueDatePicker, choTaskPri);
        texts.setAlignment(Pos.CENTER);

        // - Task Grid Setup ---
        taskGrid.setHgap(10);
        taskGrid.setVgap(10);
        // Header labels for the grid
        taskGrid.add(new Label("Task"){{setFont(mainLabelFont);}}, 0, 0);
        taskGrid.add(new Label("Category"){{setFont(mainLabelFont);}}, 1, 0);
        taskGrid.add(new Label("Due Date"){{setFont(mainLabelFont);}}, 2, 0);
        taskGrid.add(new Label("Priority"){{setFont(mainLabelFont);}}, 3, 0);
        taskGrid.add(new Label("Done"){{setFont(mainLabelFont);}}, 4, 0);
        taskGrid.setGridLinesVisible(true);
        taskGrid.setAlignment(Pos.CENTER);

        
        Button submitButton = new Button("Add/Update Task");
        submitButton.setOnAction(e -> {
            String selectedType = getSelectedCategoryType();
            if (selectedType.isEmpty()) {
                taskValidator.showErrorMessage("Please select a task type.");
                return;
            }

            Task newTask = null;
            LocalDate date = dueDatePicker.getValue();
            String priority = choTaskPri.getValue();
            String name = taskName.getText();
            
            // Create the right kind of task object based on what the user picked
            switch(selectedType) {
                case "Work":
                    newTask = new WorkTask(name, "Work", date, priority, false);
                    break;
                case "Personal":
                     newTask = new PersonalTask(name, "Personal", date, priority, false);
                    break;
                case "Reminder":
                    // Reminder is simpler, has default category/priority
                    newTask = new ReminderTask(name, date,priority, false);
                    break;
            }

            int ignoreIndex = isEditing ? (editingRow - 1) : -1;
            // Validate
            if (taskValidator.validateTask(newTask, ignoreIndex)) {
                if (isEditing) {
                    // If editing, update the existing task in the list
                    newTask.setDone(currentTasks.get(ignoreIndex).isDone()); // Keep the old 'done' status
                    currentTasks.set(ignoreIndex, newTask);
                    isEditing = false; // Reset editing flags
                    editingRow = -1;
                } else {
                    // If not editing, just add the new task to the list
                    currentTasks.add(newTask);
                }
                updateGrid(readWrite); 
                readWrite.saveAllTasks(currentTasks); 
                clearInputFields(); 
                taskValidator.showSuccessMessage("Task saved successfully!");
            }
        });

        // --- Bottom Buttons ---
        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> primaryStage.setScene(buildIntroScene(primaryStage, readWrite)));

        HBox buttonsBox = new HBox(20, backButton);
        buttonsBox.setAlignment(Pos.CENTER);

        HBox inputBox = new HBox(10, texts, submitButton);
        inputBox.setAlignment(Pos.CENTER);
        
        // --- Put it all together in the root VBox ---
        VBox root = new VBox(20, header, searchBar, inputBox, taskGrid, buttonsBox);
        root.setPadding(new Insets(20));
        
        // --- Search Button Logic ---
        searchButton.setOnAction(e -> {
            ArrayList<Task> filtered = tasksSearcher.searchTasks(currentTasks, search.getText());
            // Redraw the grid with only the filtered results
            taskGrid.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
            currentRow = 1;
            for(Task t : filtered) {
                addTaskRowToGrid(currentRow++, t, readWrite);
            }
        });
        clearSearchButton.setOnAction(e -> {
            search.clear(); // Empty the search bar
            updateGrid(readWrite); // Show all tasks again
        });

        return new Scene(root, 1000, 700);
    }
    
    /**
     * Handles creating a new task list file.
     */
    private void handleNewTaskList(Stage primaryStage, TextReadAndWrite readWrite) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Task File");
        dialog.setHeaderText("Create a new file to store your tasks.");
        dialog.setContentText("Enter file name:");
        dialog.showAndWait().ifPresent(filename -> {
            if (!filename.trim().isEmpty()) {
                readWrite.setFile(new File(filename.trim() + ".txt"));
                currentTasks.clear(); // Start with a fresh list
                updateGrid(readWrite); // Clear the display
                primaryStage.setScene(buildMainScene(primaryStage, readWrite)); // Go to the main screen
            }
        });
    }

    /**
     * Handles loading an existing task list from a file.
     */
    private void handleLoadTaskList(Stage primaryStage, TextReadAndWrite readWrite) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Task File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        
        if (selectedFile != null) {
            readWrite.setFile(selectedFile);
            currentTasks.clear(); // Clear any existing tasks before loading
            
            ArrayList<String[]> savedTasks = readWrite.readFromSave();
            for (String[] fields : savedTasks) {
                if (fields.length < 6) continue; // Skip any weird/broken lines in the file
                
                String type = fields[0];
                String name = fields[1];
                String category = fields[2];
                LocalDate date = LocalDate.parse(fields[3]);
                String priority = fields[4];
                boolean done = Boolean.parseBoolean(fields[5]);
                
                Task loadedTask = null;
                // Re-create the correct Task subclass based on the type saved in the file
                switch(type) {
                    case "WorkTask":
                        loadedTask = new WorkTask(name, category, date, priority, done);
                        break;
                    case "PersonalTask":
                        loadedTask = new PersonalTask(name, category, date, priority, done);
                        break;
                    case "ReminderTask":
                        loadedTask = new ReminderTask(name, date, priority, done);
                        break;
                }
                if(loadedTask != null) {
                    currentTasks.add(loadedTask);
                }
            }
            taskValidator.setTaskList(currentTasks); // Update the validator with the loaded list
            updateGrid(readWrite); // Refresh the display
            primaryStage.setScene(buildMainScene(primaryStage, readWrite)); // Go to the main screen
        }
    }

    //Start Method
    @Override
    public void start(Stage primaryStage) {
        TextReadAndWrite readWrite = new TextReadAndWrite();
        primaryStage.setTitle("Task Manager");
        primaryStage.setScene(buildIntroScene(primaryStage, readWrite)); // Start at the intro screen
        primaryStage.show();
    }
}
