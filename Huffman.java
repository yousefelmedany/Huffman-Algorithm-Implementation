import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


class Huffman_Node{
    byte data;
    int frequency;
    String codelength;
    Huffman_Node leftChild;
    Huffman_Node rightChild;

    public Huffman_Node(byte d, int f, String c, Huffman_Node l, Huffman_Node r){
            this.data=d;
            this.codelength=c;
            this.frequency = f;
            this.leftChild = l;
            this.rightChild = r;
    }
}

class HuffmanComparator implements Comparator<Huffman_Node>{
    public int compare(Huffman_Node i, Huffman_Node j){
        return Integer.compare(i.frequency, j.frequency);
    }
}


public class Huffman {

    public static void GenerateCodes(Huffman_Node root, Map<Byte, String> mylist) 
    { 
        if (root.leftChild == null && root.rightChild == null) { 
            mylist.put(root.data, root.codelength);
              return; 
        }
        root.leftChild.codelength=root.codelength+"0"; 
        root.rightChild.codelength=root.codelength+"1"; 
        
        GenerateCodes(root.leftChild, mylist); 
        GenerateCodes(root.rightChild, mylist); 
    } 
   
    public static Huffman_Node HuffmanAlgorithm(PriorityQueue<Huffman_Node> queue){
        
        while((!queue.isEmpty())&& (queue.size()>1)){
            Huffman_Node left = queue.peek();
            queue.poll();
            Huffman_Node right = queue.peek();
            queue.poll(); 
            Huffman_Node Parent = new Huffman_Node((byte)'0', left.frequency+right.frequency, null, left, right);
            queue.add(Parent);
        }
        return queue.peek();
    }
    
    public static void Compress(int n, String Filepath) throws IOException{
        
        Map<Byte, Integer> byteFrequencyMap = new HashMap<>();    
        PriorityQueue<Huffman_Node> HuffmanPriorityQueue = new PriorityQueue<>(new HuffmanComparator());

        BufferedInputStream StreamReader = new BufferedInputStream(new FileInputStream(Filepath));
        long fileLength = StreamReader.available();
        int size = 102400;
        byte[] buffer = new byte[size];
        int bytesRead;
         
        

        long start = System.currentTimeMillis();      
        while ((bytesRead = StreamReader.read(buffer)) != -1) {
            for(int i= 0;i<bytesRead;i++){
                    byteFrequencyMap.put(buffer[i], byteFrequencyMap.getOrDefault(buffer[i], 0) + 1);
            }
                        
        }
        StreamReader.close();

        long end = System.currentTimeMillis();
        System.out.println("Time of section 1 is "+(end-start)+" ms");

        
        
        
        start = System.currentTimeMillis();
        List<Map.Entry<Byte, Integer>> sortedList = new ArrayList<>(byteFrequencyMap.entrySet());
        Collections.sort(sortedList, Map.Entry.comparingByValue());
        end = System.currentTimeMillis();
        System.out.println("Time of section 2 is "+(end-start)+" ms");


        // for (Map.Entry<Byte, Integer> entry : sortedList) {
        //     System.out.println("Byte: " + entry.getKey()+ ", Frequency: " + entry.getValue());
        // }

        for (Map.Entry<Byte, Integer> entry : sortedList) {
            Huffman_Node newnode = new Huffman_Node(entry.getKey(), entry.getValue(),"", null,null);
            HuffmanPriorityQueue.add(newnode);          
        }
        // for (Huffman_Node entry : HuffmanPriorityQueue) {
        //     System.out.println("Key is "+entry.data+" Frequency is "+entry.frequency);
        // }

        start = System.currentTimeMillis();
        
        Huffman_Node TreeRoot = HuffmanAlgorithm(HuffmanPriorityQueue);
        TreeRoot.codelength="";
        Map<Byte, String> HuffmanMap = new HashMap<>();
        GenerateCodes(TreeRoot, HuffmanMap);
        
        end = System.currentTimeMillis();
        System.out.println("Time of section 3 is "+(end-start)+" ms");


        // for (Map.Entry<Byte, String> entry : HuffmanMap.entrySet()) {
        //     System.out.println("Key is "+entry.getKey()+" CodeWord is "+entry.getValue());
        // }

        File newFile = new File(Filepath);
        String NewPath = newFile.getParent()+"\\20012293."+n+"."+newFile.getName()+".hc";
        BufferedWriter writer = new BufferedWriter(new FileWriter(NewPath));
        
            
        start = System.currentTimeMillis();
        writer.write(Integer.toString(n)+"\n");
        writer.write(Integer.toString(HuffmanMap.size())+"\n");
        for (Map.Entry<Byte, String> entry : HuffmanMap.entrySet()) {
            // System.out.println(entry.getKey()+" "+(char)(entry.getKey() & 0xFF)+" "+entry.getValue());
            writer.write(entry.getKey());    
            writer.write(entry.getValue()+"\n");
        }
        end = System.currentTimeMillis();
        System.out.println("Time of section 4 is "+(end-start)+" ms");


        start = System.currentTimeMillis();
        
        StreamReader = new BufferedInputStream(new FileInputStream(Filepath));
        String s ="";
        while ((bytesRead = StreamReader.read(buffer)) != -1) {
            for(int i= 0;i<bytesRead;i++){
                s+=HuffmanMap.get(buffer[i]);
            }   
            writer.write(s);  
            s="";
        }
        end = System.currentTimeMillis();
        System.out.println("Time of section 5 is "+(end-start)+" ms");
        writer.close();

        System.out.println("Map Size is "+HuffmanMap.size());
    }
    
    public static void deCompress(String path) throws IOException{
        BufferedInputStream StreamReader = new BufferedInputStream(new FileInputStream(path));
        long fileLength = StreamReader.available();
        int size = 102400;
        byte[] buffer = new byte[size];
        int bytesRead;
        Map<Byte, String> HuffmanMap = new HashMap<>();
        int n=0, MapSize=0, entries_read=0;

        boolean hasheader=true, FirstChunk=true;
        while ((bytesRead = StreamReader.read(buffer)) != -1) {
            System.out.println(bytesRead);
            if(hasheader){
                int i=0;
                if(FirstChunk){
                    while(buffer[i]!='\n'){
                        i++;
                    }
                    byte[] subsetBytes = Arrays.copyOfRange(buffer, 0, i);
                    String subsetString = new String(subsetBytes, StandardCharsets.UTF_8);
                    n = Integer.parseInt(subsetString);
                    i++;
                    int k=i;

                    while(buffer[i]!='\n'){
                        i++;
                    }
                    subsetBytes = Arrays.copyOfRange(buffer, k, i);
                    subsetString = new String(subsetBytes, StandardCharsets.UTF_8);
                    MapSize = Integer.parseInt(subsetString);
                    FirstChunk = false;
                }
                
                // i++;
                // while((entries_read<MapSize)&&(i<bytesRead)){
                //     int k= i+1;
                //     while(buffer[k]!='\n'){
                //         j++;
                //     }
                //     HuffmanMap.put(buffer[i], new String(Arrays.copyOfRange(buffer,k,j), StandardCharsets.UTF_8));
                // }
                    hasheader = false;
            }
        }    
        System.out.println("N is "+n);
        System.out.println("Map Size is "+MapSize);
    }


public static void main(String[] args){
        String Path = args[1];
        // int n = Integer.parseInt(args[2]);
        try {
            
            long start = System.currentTimeMillis();
            // Compress(n, Path);
            deCompress(Path);
            long end = System.currentTimeMillis();
            System.out.println("deCompression Time is "+(end-start));
            
        } catch (IOException e) {
            e.printStackTrace();
        }    
    }
}
