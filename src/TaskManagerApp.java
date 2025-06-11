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

public class TaskManagerApp extends Application {
    private GridPane taskGrid = new GridPane();
    private int currentRow = 1; // Start at row 1 to leave room for headers
    private final ArrayList<Task> taskList = new ArrayList<>();
    private final ArrayList<Task> currentTasks = new ArrayList<>();
    public TaskValidator taskValidator = new TaskValidator(currentTasks);
    private boolean fileCheckState = false;
    private boolean isEditing = false;
    private int editingRow = -1;
    private TasksSearcher tasksSearcher = new TasksSearcher();
    ArrayList<String[]> allTasks = new ArrayList<>();
    ComboBox<String> choTaskPri = new ComboBox<String>();

    private ToggleGroup categoryGroup = new ToggleGroup();
    private RadioButton assignmentRadio = new RadioButton("Assignment");
    private RadioButton examRadio = new RadioButton("Exam");
    private RadioButton quizRadio = new RadioButton("Quiz");
    private RadioButton pracRadio = new RadioButton("Practice");
    private RadioButton otherRadio = new RadioButton("Other");

    public static void main(String[] args) {
        launch(args);
    }

    private Scene buildIntroScene(Button startButton, Button loadButton, Button viewTasksButton) {
        Label introLabel = new Label("Welcome to Task Manager!");
        introLabel.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 32));
        VBox introLayout = new VBox(30, introLabel, startButton, loadButton, viewTasksButton);
        introLayout.setAlignment(Pos.CENTER);
        return new Scene(introLayout, 800, 600);
    }

    private void saveAllTasksToFile(TextReadAndWrite readWrite) {
        readWrite.saveAllTasks(currentTasks);
    }

    private String getSelectedCategory() {
        RadioButton selected = (RadioButton) categoryGroup.getSelectedToggle();
        if (selected != null)
            return selected.getText();
        else
            return "";
    }

    private void setSelectedCategory(String category) {
        categoryGroup.selectToggle(null);
        switch (category) {
            case "Assignment":
                categoryGroup.selectToggle(assignmentRadio);
                break;
            case "Exam":
                categoryGroup.selectToggle(examRadio);
                break;
            case "Quiz":
                categoryGroup.selectToggle(quizRadio);
                break;
            case "Practice":
                categoryGroup.selectToggle(pracRadio);
                break;
            case "Other":
                categoryGroup.selectToggle(otherRadio);
                break;
            default:
                categoryGroup.selectToggle(otherRadio);
                break;
        }
    }

    private Scene buildTaskListScene(Stage primaryStage, ArrayList<String[]> allTasks, Scene introScene, File selectedFile) {
        Label taskListHeader = new Label("All Tasks");
        taskListHeader.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 32));
        taskListHeader.setTextFill(Color.TAN);
        taskListHeader.setMaxWidth(Double.MAX_VALUE);
        taskListHeader.setAlignment(Pos.CENTER);

        ListView<String> taskListView = new ListView<>();

        if (allTasks != null) {
            taskListView.getItems().add("=== Tasks from file: " + selectedFile + " ===");
            for (String[] task : allTasks) {
                if (task.length >= 4) {
                    String taskDisplay = String.format(
                            "Task: %-50s | Category: %-40s | Due: %-20s | Priority: %s",
                            task[0].trim(), task[1].trim(), task[2].trim(), task[3].trim()
                    );
                    taskListView.getItems().add(taskDisplay);
                } else {
                    taskListView.getItems().add("No tasks available.");
                }
            }
        }

        Button backButton = new Button("Back");
        backButton.setPrefWidth(100);
        backButton.setOnAction(e -> primaryStage.setScene(introScene));

        Button exitButton = new Button("Exit");
        exitButton.setPrefWidth(100);
        exitButton.setOnAction(e -> {
            taskValidator.confirmExit();
        });

        HBox buttonsBox = new HBox(20, exitButton, backButton);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, taskListHeader, taskListView, buttonsBox);
        layout.setStyle("-fx-background-color: #FAF0E6;");
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        return new Scene(layout, 800, 600);
    }

    // --- Helper method to add a task row to the grid ---
    private void addTaskRowToGrid(
        int row, Task task, Font font, TextField taskName, DatePicker dueDatePicker, ComboBox<String> choTaskPri,
        GridPane taskGrid, ArrayList<Task> currentTasks, TextReadAndWrite readWrite, TaskValidator taskValidator, ToggleGroup categoryGroup
    ) {
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

        taskGrid.add(name, 0, row);
        taskGrid.add(cat, 1, row);
        taskGrid.add(date1, 2, row);
        taskGrid.add(pri, 3, row);
        taskGrid.add(doneCheck, 4, row);

        Button editBtn = new Button("Edit");
        Button deleteBtn = new Button("Delete");
        taskGrid.add(editBtn, 5, row);
        taskGrid.add(deleteBtn, 6, row);

        final int rowToEdit = row;
        final int taskIndex = row - 1;

        doneCheck.setOnAction(ev -> {
            if (taskIndex < currentTasks.size()) {
                currentTasks.get(taskIndex).setDone(doneCheck.isSelected());
                saveAllTasksToFile(readWrite);
            }
        });

        editBtn.setOnAction(ev -> {
            taskName.setText(task.getTaskName());
            setSelectedCategory(task.getCategory());
            dueDatePicker.setValue(task.getDueDate());
            choTaskPri.setValue(task.getPriority());
            isEditing = true;
            editingRow = rowToEdit;
            // Only remove from grid for UI clarity, NOT from currentTasks!
            taskGrid.getChildren().removeIf(node -> {
                Integer rowIndex = GridPane.getRowIndex(node);
                return rowIndex != null && rowIndex == rowToEdit;
            });
        });


        deleteBtn.setOnAction(ev -> {
            if (taskValidator.confirmDelete()) {
                if (taskIndex < currentTasks.size()) {
                    currentTasks.remove(taskIndex);
                }
                taskGrid.getChildren().removeIf(node -> {
                    Integer rowIndex = GridPane.getRowIndex(node);
                    return rowIndex != null && rowIndex == rowToEdit;
                });
                taskGrid.getChildren().forEach(node -> {
                    Integer rowIndex = GridPane.getRowIndex(node);
                    if (rowIndex != null && rowIndex > rowToEdit) {
                        GridPane.setRowIndex(node, rowIndex - 1);
                    }
                });
                currentRow--;
                saveAllTasksToFile(readWrite);
            }
        });
    }

    // --- displayFilteredTasks now just uses the helper ---
    private void displayFilteredTasks(
        String searchQuery, TasksSearcher searcher, Font font, TextReadAndWrite rw,
        TextField tn, VBox categoryBox, DatePicker dueDatePicker, ComboBox<String> choTaskPri, TextField searchField
    ) {
        ArrayList<Task> filteredTasks = searcher.searchTasks(currentTasks, searchQuery);

        // Clear the current display except header
        taskGrid.getChildren().removeIf(node -> {
            Integer rowIndex = GridPane.getRowIndex(node);
            return rowIndex != null && rowIndex > 0;
        });

        for (int i = 0; i < filteredTasks.size(); i++) {
            Task task = filteredTasks.get(i);
            addTaskRowToGrid(
                i + 1, task, font, tn, dueDatePicker, choTaskPri,
                taskGrid, currentTasks, rw, taskValidator, categoryGroup
            );
        }
    }

    @Override
    public void start(Stage primaryStage) {
        TextReadAndWrite readWrite = new TextReadAndWrite();

        Button newTasksButton = new Button("New Tasks");
        newTasksButton.setPrefWidth(100);
        Button loadButton = new Button("Load");
        loadButton.setPrefWidth(100);
        Button viewTasksButton = new Button("View All Tasks");
        viewTasksButton.setPrefWidth(100);
        Scene introScene = buildIntroScene(newTasksButton, loadButton, viewTasksButton);

        viewTasksButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Task File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                readWrite.setFile(selectedFile);
                fileCheckState = true;
                primaryStage.setScene(introScene);
                allTasks = readWrite.readFromSave();
            }

            Scene taskListScene = buildTaskListScene(primaryStage, allTasks, introScene, selectedFile);
            primaryStage.setScene(taskListScene);
        });

        Label header = new Label("Task Manager");
        header.setFont(Font.font("Book Antiqua", FontWeight.BOLD, 32));
        header.setTextFill(Color.TAN);
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        Font mainLabelFont = Font.font("Book Antiqua", FontWeight.BOLD, 28);
        Font subLabelFont = Font.font("Book Antiqua", 25);

        TextField search = new TextField();
        TextField taskName = new TextField();

        assignmentRadio.setToggleGroup(categoryGroup);
        examRadio.setToggleGroup(categoryGroup);
        quizRadio.setToggleGroup(categoryGroup);
        pracRadio.setToggleGroup(categoryGroup);
        otherRadio.setToggleGroup(categoryGroup);

        VBox categoryRadioBox = new VBox(5, assignmentRadio, examRadio, quizRadio, pracRadio, otherRadio);
        categoryRadioBox.setAlignment(Pos.CENTER_LEFT);

        DatePicker dueDatePicker = new DatePicker();

        choTaskPri.setPromptText("Choose Task Priority");
        choTaskPri.getItems().addAll("High", "Medium", "Low");

        taskName.setPromptText("Task Name");
        dueDatePicker.setPromptText("Select Due Date");
        search.setPromptText("Enter Search");

        Button searchButton = new Button("Search");
        searchButton.setPrefWidth(100);
        HBox searchBar = new HBox(10, search, searchButton);
        searchBar.setAlignment(Pos.CENTER);
        searchBar.setMaxWidth(Double.MAX_VALUE);

        Runnable displayFilteredTasksRunnable = () -> {
            String searchQuery = search.getText();
            displayFilteredTasks(searchQuery, tasksSearcher, subLabelFont, readWrite,
                    taskName, categoryRadioBox, dueDatePicker, choTaskPri, search);
        };
        searchButton.setOnAction(e -> displayFilteredTasksRunnable.run());

        HBox texts = new HBox(10, taskName, categoryRadioBox, dueDatePicker, choTaskPri);

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
        Label done = new Label("Done");
        done.setFont(mainLabelFont);

        taskGrid.add(task, 0, 0);
        taskGrid.add(cate, 1, 0);
        taskGrid.add(due, 2, 0);
        taskGrid.add(prio, 3, 0);
        taskGrid.add(done, 4, 0);

        taskGrid.setGridLinesVisible(true);
        taskGrid.setAlignment(Pos.CENTER);

        Button submitButton = new Button("Add Task");
        submitButton.setOnAction(e -> {
            LocalDate date = dueDatePicker.getValue();

            Task newTask = new Task(taskName.getText(), getSelectedCategory(), date, choTaskPri.getValue(), false);
            int ignoreIndex = isEditing ? (editingRow - 1) : -1;
            if (taskValidator.validateTask(newTask, ignoreIndex)) {
                int rowToUse;
                if (isEditing) {
                    rowToUse = editingRow;
                    // Use the old done status if needed
                    boolean currentDoneStatus = currentTasks.get(rowToUse - 1).isDone();
                    newTask = new Task(taskName.getText(), getSelectedCategory(), date, choTaskPri.getValue(), currentDoneStatus);
                    currentTasks.set(rowToUse - 1, newTask);
                    taskGrid.getChildren().removeIf(node -> {
                        Integer rowIndex = GridPane.getRowIndex(node);
                        return rowIndex != null && rowIndex == rowToUse;
                    });
                    isEditing = false;
                    editingRow = -1;
                } else {
                    rowToUse = currentRow;
                    currentTasks.add(newTask);
                    currentRow++;
                }
                addTaskRowToGrid(rowToUse, newTask, subLabelFont, taskName, dueDatePicker, choTaskPri,
                        taskGrid, currentTasks, readWrite, taskValidator, categoryGroup);

                taskValidator.showSuccessMessage("Task successfully added!");
                readWrite.saveAllTasks(currentTasks); // Save all tasks to file
                taskName.clear();
                categoryGroup.selectToggle(null);
                dueDatePicker.setValue(null);
                choTaskPri.setValue(null);
            }
        });

        Button exitButton = new Button("Exit");
        exitButton.setPrefWidth(100);
        exitButton.setOnAction(e -> {
            taskValidator.confirmExit();
        });

        Button backButton = new Button("Back");
        backButton.setPrefWidth(100);
        backButton.setOnAction(e -> primaryStage.setScene(introScene));

        HBox buttonsBox = new HBox(20, exitButton, backButton);
        buttonsBox.setAlignment(Pos.CENTER);

        HBox inputBox = new HBox(10, texts, submitButton);
        inputBox.setAlignment(Pos.CENTER);
        VBox root = new VBox(20, header, searchBar, inputBox, taskGrid, buttonsBox);
        root.setStyle("-fx-background-color: #FAF0E6;");
        Scene scene = new Scene(root, 800, 600);

        Scene mainScene = scene;

        newTasksButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("New Task File");
            dialog.setHeaderText("Create New Task File");
            dialog.setContentText("Enter a name for your new task file:");

            dialog.showAndWait().ifPresent(filename -> {
                if (!filename.trim().isEmpty()) {
                    readWrite.setFile(new File(filename.trim() + ".txt"));
                    fileCheckState = true;
                    currentTasks.clear();
                    taskGrid.getChildren().removeIf(node -> {
                        Integer rowIndex = GridPane.getRowIndex(node);
                        return rowIndex != null && rowIndex > 0;
                    });
                    currentRow = 1;
                    primaryStage.setScene(mainScene);
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

                currentTasks.clear();
                taskGrid.getChildren().removeIf(node -> {
                    Integer rowIndex = GridPane.getRowIndex(node);
                    return rowIndex != null && rowIndex > 0;
                });
                currentRow = 1;

                primaryStage.setScene(mainScene);
                ArrayList<String[]> savedTasks = readWrite.readFromSave();
                if (savedTasks != null) {
                    for (String[] taskFields : savedTasks) {
                        if (taskFields.length >= 4) {
                            boolean isTaskDone = false;
                            if (taskFields.length >= 5) {
                                isTaskDone = Boolean.parseBoolean(taskFields[4].trim());
                            }
                            LocalDate taskDate = LocalDate.parse(taskFields[2].trim());
                            Task loadedTask = new Task(taskFields[0].trim(), taskFields[1].trim(), taskDate, taskFields[3].trim(), isTaskDone);
                            currentTasks.add(loadedTask);
                            addTaskRowToGrid(currentRow, loadedTask, subLabelFont, taskName, dueDatePicker, choTaskPri,
                                    taskGrid, currentTasks, readWrite, taskValidator, categoryGroup);
                            currentRow++;
                        }
                    }
                    taskValidator.setTaskList(currentTasks);
                }
            }
        });

        primaryStage.setTitle("Task Manager");
        primaryStage.setScene(introScene);
        primaryStage.show();
    }
}