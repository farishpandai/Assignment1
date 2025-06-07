import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;


public class TextReadAndWrite {
    private File f; // stores the current file being used

    // default constructor
    public TextReadAndWrite()
    {

    }

    // checks if a file exists, creates it if it doesn't
    // returns false if a new file was created, true if it already existed
    public boolean checkForFile(String fileName)
    {
        this.f = new File(fileName+".txt");
        try{
            if (f.createNewFile()) {
                // file didn't exist, so it was created
                return false;
            }
            else{
                // file already exists
                System.out.println("File already Exist");
                return true;
            }
        }
        catch(IOException e)
        {
            // prints error if something goes wrong
            System.out.println("an error has occured:"+e.getMessage());
            return false;
        }
    }
    
    // writes a task's details to the file, appending to the end
    public void writeToFile(Task task)
    {
        try{
            FileWriter writeTo = new FileWriter(this.f,true); // open file in append mode
            writeTo.write(task.getTaskName()+";");
            writeTo.write(task.getCategory()+";");
            writeTo.write(task.getDueDate()+";");
            writeTo.write(task.getPriority()+";");
            writeTo.write(System.getProperty("line.separator"));
            writeTo.close();
        }
        catch(IOException e)
        {
            // you could print an error here if needed note to those yng buat error exception
        }
    }

    // reads all lines from the file, splits each line by ';', and returns them as a list
    public ArrayList<String[]> readFromSave()
    {
        ArrayList<String[]> allSplits = new ArrayList<>();
        try {
            Scanner scan = new Scanner(this.f);
            while (scan.hasNextLine()) {
                String test = scan.nextLine();
                String[] testSplit = test.split(";");
                allSplits.add(testSplit);
            }
            scan.close();
        } catch(IOException e) {
            // handle exception if needed
        }
        return allSplits;
    }
    
    // sets the file to be used for reading/writing
    public void setFile(File file) {
        this.f = file;
    }

}
