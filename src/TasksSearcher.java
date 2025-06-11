/**
 *
 * @author USER
 */

import java.util.ArrayList;
public class TasksSearcher {
    
    
    // returns filtered list of tasks
    public ArrayList<Task> searchTasks(ArrayList<Task> allTasks, String searchQuery) {
        ArrayList<Task> filteredTasks = new ArrayList<>();
        
        // If search is empty, return all tasks
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return new ArrayList<>(allTasks); // Return copy of all tasks
        }
        
        // Convert search query to lowercase for case-insensitive search
        String query = searchQuery.toLowerCase().trim();
        
        // Go through each task and check for matches
        for (Task task : allTasks) {
            if (taskMatches(task, query)) {
                filteredTasks.add(task);
            }
        }
        
        return filteredTasks;
    }
    
    // Helper method to check if a task matches the search query
    private boolean taskMatches(Task task, String query) {
        // Check all fields of the task
        return task.getTaskName().toLowerCase().contains(query) ||
               task.getCategory().toLowerCase().contains(query) ||
               task.getDueDate().toString().contains(query) ||
               task.getPriority().toLowerCase().contains(query);
    }
    
}
