import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main{   
public static void main(String[] args) throws NumberFormatException, IOException{

    String path = args[0];
    BufferedReader reader = new BufferedReader(new FileReader(path));
    int size = Integer.parseInt(reader.readLine());
    Activity[] myActivities = new Activity[size];

    for(int i=0;i<size;i++){
        String s = reader.readLine();
        String[] Values = s.split(" ");
        myActivities[i] = new Activity(Integer.parseInt(Values[0]),Integer.parseInt(Values[1]),Integer.parseInt(Values[2]));
    }
    reader.close();

    Activity_Handler mActivity_Handler = new Activity_Handler();
    int res = mActivity_Handler.Solve_Activity_Selection(myActivities);
    File newFile = new File(path);
    String newPath = path.substring(0, path.length()-7)+"_20012293.out.txt";
    mActivity_Handler.SaveResult(res,newPath);
    }
}
