import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;


class Activity{
    int start;
    int finish;
    int weight;


    public Activity(int start, int finish, int weight){
        this.start=start;
        this.finish=finish;
        this.weight=weight;
    }
}

class ActivityComparator implements Comparator<Activity>{
    public int compare(Activity a, Activity b)
    {
        return a.finish < b.finish ? -1 : a.finish == b.finish ? 0:1;

    }

}


public class Main{

      
public static int binarySearch(Activity Activities[], int lastactivity)
{
    int low = 0, high = lastactivity- 1;
 
        while (low <= high)
        {
            int mid = (low + high) / 2;
            if (Activities[mid].finish <= Activities[lastactivity].start)
            {
                if (Activities[mid + 1].finish <= Activities[lastactivity].start)
                    low = mid + 1;
                else
                    return mid;
            }
            else
                high = mid - 1;
        }
 
        return -1;
}
public static int Solve_Activity_Selection(Activity[] Activities){

Arrays.sort(Activities, new ActivityComparator());
 
int n = Activities.length;
int Weights[] = new int[n];
Weights[0] = Activities[0].weight;

for (int i=1; i<n; i++)
{
    int inclProf = Activities[i].weight;
    int NonConflictingActivity = binarySearch(Activities, i);
    if (NonConflictingActivity != -1)
        inclProf += Weights[NonConflictingActivity];

        Weights[i] = Math.max(inclProf, Weights[i-1]);
}

return Weights[n-1];

}
public static void SaveResult(int res, String path) throws IOException{
    BufferedWriter writer = new BufferedWriter(new FileWriter(path));
    writer.write("The Maximmum Weight Combination is "+ res);
    writer.newLine();
    writer.close();
}

static String ParentPath = "C:\\Users\\Mega Store\\Downloads\\Files\\";

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


int res = Solve_Activity_Selection(myActivities);
SaveResult(res, ParentPath+"test_20012293.out.txt");
}

}
