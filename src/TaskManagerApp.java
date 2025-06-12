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
            newTasksButton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: white; -fx-font-weight: bold;");

            Button loadButton = new Button("Load Task List");
            loadButton.setPrefWidth(150);
            loadButton.setStyle("-fx-background-color: #5F9EA0; -fx-text-fill: white; -fx-font-weight: bold;");

            Button viewTasksButton = new Button("View All Tasks");
            viewTasksButton.setPrefWidth(150);
            viewTasksButton.setStyle("-fx-background-color: #A0522D; -fx-text-fill: white; -fx-font-weight: bold;"); // Sienna color

            newTasksButton.setOnAction(e -> handleNewTaskList(primaryStage, readWrite));
            loadButton.setOnAction(e -> handleLoadTaskList(primaryStage, readWrite));
            viewTasksButton.setOnAction(e -> handleViewAllTasks(primaryStage, readWrite));

            VBox introLayout = new VBox(20, introLabel, newTasksButton, loadButton, viewTasksButton);
            introLayout.setAlignment(Pos.CENTER);
            introLayout.setStyle("-fx-background-color: #F0F8FF;"); // AliceBlue background
            return new Scene(introLayout, 800, 600);
        }
        
        // Build a method for the Task List Screen
        private Scene buildTaskListScene(Stage primaryStage, ArrayList<String[]> allTasks, Scene introScene, File selectedFile) {
            
            // Title label
            Label taskListHeader = new Label("All Tasks");
            taskListHeader.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 32));
            taskListHeader.setTextFill(Color.TAN);  
            taskListHeader.setMaxWidth(Double.MAX_VALUE);  
            taskListHeader.setAlignment(Pos.CENTER);
        
            // Create the ListView
            ListView<String> taskListView = new ListView<>();
            taskListView.setStyle("-fx-font-family: 'Courier New';"); // Use a monospaced font for alignment

            // Check and populate task list
            if (allTasks != null && !allTasks.isEmpty()) {
                taskListView.getItems().add("=== Tasks from file: " + selectedFile.getAbsolutePath() + " ===");
                for (String[] task : allTasks) {
                    // Ensure the task array has enough elements before accessing them
                    if (task.length >= 6) { // Now expecting at least 6 fields from the save file
                        String taskDisplay = String.format(
                            "Task: %-40s | Category: %-20s | Due: %-15s | Priority: %s",
                            task[1].trim(), task[2].trim(), task[3].trim(), task[4].trim()
                        );
                        taskListView.getItems().add(taskDisplay);
                    }
                }
            } else {
                taskListView.getItems().add("No tasks available in the selected file.");
            }


            // Add back button and event on click for back
            Button backButton = new Button("Back");
            backButton.setStyle("-fx-background-color: #778899; -fx-text-fill: white;");
            backButton.setPrefWidth(100);
            backButton.setOnAction(e -> 
                    primaryStage.setScene(introScene)
            );
            
            //Add exit and event on click for exit
            Button exitButton = new Button("Exit");
            exitButton.setStyle("-fx-background-color: #B22222; -fx-text-fill: white;");
            exitButton.setPrefWidth(100); 
            exitButton.setOnAction(e -> {
                taskValidator.confirmExit();
            });
            
            // Layout setup (create a HBox for the Exit button & Back button)
            HBox buttonsBox = new HBox(20,exitButton, backButton);    
            buttonsBox.setAlignment(Pos.CENTER);

            // Layout scene
            VBox layout = new VBox(20, taskListHeader,taskListView, buttonsBox);
            layout.setStyle("-fx-background-color: #F0F8FF;");   
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(30));

            return new Scene(layout, 950, 600);
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
            
            // Color code priority
            switch (task.getPriority()) {
                case "High":
                    pri.setTextFill(Color.RED);
                    break;
                case "Medium":
                    pri.setTextFill(Color.ORANGE);
                    break;
                case "Low":
                    pri.setTextFill(Color.GREEN);
                    break;
            }

            // Add everything to the grid at the specified row
            taskGrid.add(name, 0, row);
            taskGrid.add(cat, 1, row);
            taskGrid.add(date1, 2, row);
            taskGrid.add(pri, 3, row);
            taskGrid.add(doneCheck, 4, row);

            Button editBtn = new Button("Edit");
            editBtn.setStyle("-fx-background-color: #1E90FF; -fx-text-fill: white;"); // DodgerBlue
            Button deleteBtn = new Button("Delete");
            deleteBtn.setStyle("-fx-background-color: #DC143C; -fx-text-fill: white;"); // Crimson
            
            HBox actionBox = new HBox(5, editBtn, deleteBtn);
            actionBox.setAlignment(Pos.CENTER);
            
            taskGrid.add(actionBox, 5, row);


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
            search.setPrefWidth(300);
            Button searchButton = new Button("Search");
            searchButton.setPrefWidth(100);
            searchButton.setStyle("-fx-background-color: #2E8B57; -fx-text-fill: white;"); // SeaGreen
            Button clearSearchButton = new Button("Clear");
            clearSearchButton.setPrefWidth(100);
            clearSearchButton.setStyle("-fx-background-color: #D2B48C; -fx-text-fill: black;"); // Tan
            
            HBox searchBar = new HBox(15, search, searchButton, clearSearchButton);
            searchBar.setAlignment(Pos.CENTER);

            // -- Input Fields Setup ---
            workRadio.setToggleGroup(categoryGroup);
            personalRadio.setToggleGroup(categoryGroup);
            reminderRadio.setToggleGroup(categoryGroup);
            VBox categoryRadioBox = new VBox(10, workRadio, personalRadio, reminderRadio);
            categoryRadioBox.setPadding(new Insets(5));

            choTaskPri.setPromptText("Priority");
            choTaskPri.getItems().addAll("High", "Medium", "Low");
            taskName.setPromptText("Task Name");
            taskName.setPrefWidth(200);
            dueDatePicker.setPromptText("Due Date");

            HBox texts = new HBox(15, taskName, categoryRadioBox, dueDatePicker, choTaskPri);
            texts.setAlignment(Pos.CENTER);

            // - Task Grid Setup ---
            taskGrid.setHgap(20);
            taskGrid.setVgap(15);
            taskGrid.setPadding(new Insets(15));
            taskGrid.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #C0C0C0; -fx-border-radius: 5; -fx-background-radius: 5;");
            
            // Header labels for the grid
            taskGrid.add(new Label("Task"){{setFont(mainLabelFont);}}, 0, 0);
            taskGrid.add(new Label("Category"){{setFont(mainLabelFont);}}, 1, 0);
            taskGrid.add(new Label("Due Date"){{setFont(mainLabelFont);}}, 2, 0);
            taskGrid.add(new Label("Priority"){{setFont(mainLabelFont);}}, 3, 0);
            taskGrid.add(new Label("Done"){{setFont(mainLabelFont);}}, 4, 0);
            taskGrid.add(new Label("Actions"){{setFont(mainLabelFont);}}, 5, 0); // Added Actions header
            taskGrid.setGridLinesVisible(true);
            taskGrid.setAlignment(Pos.CENTER);

            
            Button submitButton = new Button("Add/Update Task");
            submitButton.setStyle("-fx-background-color: #006400; -fx-text-fill: white; -fx-font-weight: bold;");
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
            backButton.setStyle("-fx-background-color: #778899; -fx-text-fill: white;"); 
            backButton.setOnAction(e -> primaryStage.setScene(buildIntroScene(primaryStage, readWrite)));

            
            Button exitButton = new Button("Exit");
            exitButton.setStyle("-fx-background-color: #B22222; -fx-text-fill: white;");
            exitButton.setOnAction(e -> taskValidator.confirmExit());

            HBox buttonsBox = new HBox(20, backButton, exitButton);
            buttonsBox.setAlignment(Pos.CENTER);

            HBox inputBox = new HBox(20, texts, submitButton);
            inputBox.setAlignment(Pos.CENTER);
            
            // --- Put it all together in the root VBox ---
            VBox root = new VBox(25, header, searchBar, inputBox,taskGrid, buttonsBox);
            root.setPadding(new Insets(25));
            root.setStyle("-fx-background-color: #F5F5F5;"); // WhiteSmoke
            
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

            return new Scene(root, 1200, 800);
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
        * Handles the "View All Tasks" button click.
        * This method opens a file chooser and displays the raw task data in a ListView.
        */
        private void handleViewAllTasks(Stage primaryStage, TextReadAndWrite readWrite) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Task File to View");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            
            if (selectedFile != null) {
                readWrite.setFile(selectedFile);
                ArrayList<String[]> savedTasks = readWrite.readFromSave();
                
                // Re-create the intro scene to pass to the task list scene for the 'Back' button functionality
                Scene introScene = buildIntroScene(primaryStage, readWrite);
                Scene taskListScene = buildTaskListScene(primaryStage, savedTasks, introScene, selectedFile);
                primaryStage.setScene(taskListScene);
            }
        }

        /**
        * Handles loading an existing task list from a file for management.
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

