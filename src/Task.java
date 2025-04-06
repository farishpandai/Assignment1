import java.time.LocalDate;
public class Task {

    //Variable initilization

    private String taskName;
    private String category;
    private LocalDate dueDate;
    private String priority;

    //Constructor
    public Task()
    {
        taskName="";
        category="";
        dueDate=LocalDate.of(1, 1, 1);
        priority="";
    }
    //Constructor with input Parameter
    public Task(String taskName,String category,LocalDate dueDate,String priority)
    {
        this.taskName=taskName;
        this.category=category;
        this.dueDate=dueDate;
        this.priority=priority;
    }

    //Getters for all the variables
    public String getTaskName()
    {
        return this.taskName;
    }

    public String getCategory()
    {
        return  this.category;
    }

    public String getPriority()
    {
        return this.priority;
    }
    
    public LocalDate getDueDate()
    {
        return this.dueDate;
    }

    //setters for all the variables
    public void setTaskName(String taskName)
    {
        this.taskName=taskName;
    }
    public void setCategory(String category)
    {
        this.category=category;
    }
    public void setDueDate(LocalDate dueDate)
    {
        this.dueDate=dueDate;
    }
    public void setPriority(String priority)
    {
        this.priority=priority;
    }
    
    //Formated toString
    public String toString()
    {
        return ("This is your current task:\nName\t\tCategory\t\tPriority\t\tDue Date\n"+getTaskName()+"\t\t"+getCategory()+"\t\t\t"+getPriority()+"\t\t\t"+getDueDate());
    }

}
