import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class neww {
 
    public static void main(String[] args) throws IOException{

        // String myString = "yousef";
        // System.out.println(myString.length());
        // byte[] stringBytesUTF8 = myString.getBytes();
        // System.out.println(stringBytesUTF8.length);
        // for(byte item:stringBytesUTF8){
        //     System.out.printf("%02X ", item & 0xFF); // Masking with 0xFF to get the unsigned byte value
        //     System.out.print(String.format("%8s", Integer.toBinaryString(item & 0xFF)).replace(' ', '0')+" ");
        // }

        // byte[] subarray = Arrays.copyOfRange(stringBytesUTF8, 0, 2);

        // String resultString = new String(subarray, StandardCharsets.UTF_8);
        // System.out.println(resultString);

        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(Comparator.naturalOrder());
        priorityQueue.add(5);
        priorityQueue.add(3);
        priorityQueue.add(8);
        
        // Elements will be dequeued in ascending order (smallest first)
        for(int item:priorityQueue) {
            System.out.println(item);
        }



    }
}
