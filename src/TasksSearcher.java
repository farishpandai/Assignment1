import java.util.ArrayList;

/**
 * A simple class to handle searching through our list of tasks.
 * it just filters the main list based on a search query from the user.
 */
public class TasksSearcher {
    
    /**
     * the main search method.
     */
    public ArrayList<Task> searchTasks(ArrayList<Task> allTasks, String searchQuery) {
        ArrayList<Task> filteredTasks = new ArrayList<>();
        
        // if search bar is empty, just return all the tasks
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return new ArrayList<>(allTasks); // return a copy, not the original list
        }
        
        // convert to lowercase so the search isn't case-sensitive
        String query = searchQuery.toLowerCase().trim();
        
        // loop through every task
        for (Task task : allTasks) {
            // if the task matches our query, add it to the filtered list
            if (taskMatches(task, query)) {
                filteredTasks.add(task);
            }
        }
        
        return filteredTasks;
    }
    
    /**
     * helper method that checks if a single task matches the search query.
     * it checks all the important fields of the task.
     */
    private boolean taskMatches(Task task, String query) {
        // return true if the query is found in the name, category, due date, OR priority
        return task.getTaskName().toLowerCase().contains(query) ||
               task.getCategory().toLowerCase().contains(query) ||
               task.getDueDate().toString().contains(query) || // check date as a string
               task.getPriority().toLowerCase().contains(query);
    }
    
}
