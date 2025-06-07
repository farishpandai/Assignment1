import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class TextReadAndWrite {
    private File f;
    

    public TextReadAndWrite()
    {

    }

    public boolean checkForFile(String fileName)
    {
        this.f = new File(fileName+".txt");
        try{
        if (f.createNewFile()) {
            return false;
        }
        else{
            System.out.println("File already Exist");
            return true;
        }
    }
    catch(IOException e)
    {
        System.out.println("an error has occured:"+e.getMessage());

        return false;
    }
    }
    
    public void writeToFile(Task task)
    {
        try{
        FileWriter writeTo = new FileWriter(this.f,true);
        writeTo.write(task.getTaskName()+";");
        writeTo.write(task.getCategory()+";");
        writeTo.write(task.getDueDate()+";");
        writeTo.write(task.getPriority()+";");
        writeTo.close();
        }
        catch(IOException e)
        {

        }

    }

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
            // Handle exception if needed
        }
        return allSplits;
    }
    
    public void setFile(File file) {
        this.f = file;
    }

}
