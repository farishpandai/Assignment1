import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class handles all the file i/o (input/output).
 * it saves our tasks to a .txt file and loads them back.
 */
public class TextReadAndWrite {
    private File f; // the current file we're working with

    public TextReadAndWrite() {
        // default constructor, does nothing
    }

    /**
     * writes a single task to the file.
     * the format is important: Type;Name;Category;DueDate;Priority;isDone
     * we use semicolons ';' so it's easy to split the string later when we read it.
  
    */
    public void writeToFile(Task task) {
        try (FileWriter writeTo = new FileWriter(this.f, true)) { 
            // Save the class name (e.g., "WorkTask") as the first item.
            writeTo.write(task.getClass().getSimpleName() + ";"); 
            writeTo.write(task.getTaskName() + ";");
            writeTo.write(task.getCategory() + ";");
            writeTo.write(task.getDueDate() + ";");
            writeTo.write(task.getPriority() + ";");
            writeTo.write(task.isDone() + ";");
            writeTo.write(System.getProperty("line.separator")); // new line
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    /**
     * reads all the tasks from the save file.
     * return an arraylist where each element is a String array of a task's data.
     */
    public ArrayList<String[]> readFromSave() {
        ArrayList<String[]> allSplits = new ArrayList<>();
        if (f == null || !f.exists()) {
            return allSplits; // just return an empty list if the file doesn't exist yet
        }
        // another try-with-resources for the scanner
        try (Scanner scan = new Scanner(this.f)) { 
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                if (!line.trim().isEmpty()) { // make sure we don't read empty lines
                    allSplits.add(line.split(";")); // split the line by ';' and add it to our list
                }
            }
        } catch (IOException e) {
             System.err.println("Error reading from file: " + e.getMessage());
        }
        return allSplits;
    }
    
    // simple setter to tell the class which file to use.
    public void setFile(File file) {
        this.f = file;
    }
    
    /**
     * saves the entire list of tasks to the file.
     * it completely overwrites the file, so we have the most up-to-date list.
     */
    public void saveAllTasks(ArrayList<Task> tasks) {
        // first, clear the file by opening it in non-append mode and closing it.
        try (FileWriter clearFile = new FileWriter(this.f, false)) {
            // file is cleared. the empty try block is intentional.
        } catch (IOException e) {
             System.err.println("Error clearing file: " + e.getMessage());
        }
        
        
        for (Task task : tasks) {
            writeToFile(task);
        }
    }
}
