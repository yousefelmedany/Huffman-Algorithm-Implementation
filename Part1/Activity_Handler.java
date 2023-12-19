import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class Activity_Handler {

    class ActivityComparator implements Comparator<Activity>{
    public int compare(Activity a, Activity b)
    {
        return a.finish < b.finish ? -1 : a.finish == b.finish ? 0:1;

    }

    }



public int binarySearch(Activity Activities[], int lastactivity){
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

public int Solve_Activity_Selection(Activity[] Activities){

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

public void SaveResult(int res, String path) throws IOException{
    BufferedWriter writer = new BufferedWriter(new FileWriter(path));
    writer.write("The Maximmum Weight Combination is "+ res);
    writer.newLine();
    writer.close();
}




}
