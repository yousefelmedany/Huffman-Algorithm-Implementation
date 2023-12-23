import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
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


class HuffmanComparator implements Comparator<Huffman_Node>{
    public int compare(Huffman_Node i, Huffman_Node j){
        return Integer.compare(i.frequency, j.frequency);
    }
}


public class Huffman {
    static int Msize=0;

    //Generate the codewords of huffman tree
    public static void GenerateCodes(Huffman_Node root, Map<ByteArrayWrapper, String> mylist) 
    { 
        if (root.leftChild == null && root.rightChild == null) { 
            ByteArrayWrapper Wap = new ByteArrayWrapper(root.data);
            mylist.put(Wap, root.codelength);
            Msize+= root.data.length+root.codelength.getBytes().length;
              return; 
        }
        root.leftChild.codelength=root.codelength+"0"; 
        root.rightChild.codelength=root.codelength+"1"; 
        
        GenerateCodes(root.leftChild, mylist); 
        GenerateCodes(root.rightChild, mylist);
    } 
    
    //Construct the huffman tree then return the root node
    public static Huffman_Node HuffmanAlgorithm(PriorityQueue<Huffman_Node> queue){
        
        while((!queue.isEmpty())&& (queue.size()>1)){
            Huffman_Node left = queue.peek();
            queue.poll();
            Huffman_Node right = queue.peek();
            queue.poll(); 
            Huffman_Node Parent = new Huffman_Node(null, left.frequency+right.frequency, null, left, right);
            queue.add(Parent);
        }
        return queue.peek();
    }

    //Compress a given file using its absoulte path and n
    public static stats Compress(int n, String Filepath) throws IOException{
        
        Map<ByteArrayWrapper, Integer> byteFrequencyMap = new HashMap<>();    
        PriorityQueue<Huffman_Node> HuffmanPriorityQueue = new PriorityQueue<>(new HuffmanComparator());

        BufferedInputStream StreamReader = new BufferedInputStream(new FileInputStream(Filepath));
        int filesize=0;
        int size = (102400000/n)*n;
        byte[] buffer = new byte[size];
        int bytesRead;
        
        while ((bytesRead = StreamReader.read(buffer)) != -1) {
            filesize+=bytesRead;  
            int i;
            for (i = 0; i < (bytesRead / n) * n; i += n) {
                ByteArrayWrapper key = new ByteArrayWrapper(Arrays.copyOfRange(buffer, i, i + n));
                byteFrequencyMap.put(key, byteFrequencyMap.getOrDefault(key, 0) + 1);
                }
            if (i != bytesRead) {
                ByteArrayWrapper key = new ByteArrayWrapper(Arrays.copyOfRange(buffer, i, bytesRead));
                byteFrequencyMap.put(key, byteFrequencyMap.getOrDefault(key, 0) + 1);
            
            }            
        }
        StreamReader.close();    
        
        List<Map.Entry<ByteArrayWrapper, Integer>> sortedList = new ArrayList<>(byteFrequencyMap.entrySet());
        Collections.sort(sortedList, Map.Entry.comparingByValue());

        for (Map.Entry<ByteArrayWrapper, Integer> entry : sortedList) {
            Huffman_Node newnode = new Huffman_Node(entry.getKey().getarray(), entry.getValue(),"", null,null);
            HuffmanPriorityQueue.add(newnode);          
        }
        
        Huffman_Node TreeRoot = HuffmanAlgorithm(HuffmanPriorityQueue);
        TreeRoot.codelength="";
        Map<ByteArrayWrapper, String> HuffmanMap = new HashMap<>();
        GenerateCodes(TreeRoot, HuffmanMap);

        File newFile = new File(Filepath);
        String NewPath = newFile.getParent()+"\\20012293."+n+"."+newFile.getName()+".hc";
        DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(NewPath));        
        

        long start = System.currentTimeMillis();
        byte[] valconverter;
        String value;
        //Writing the metadata
        outputStream.writeInt(filesize);
        outputStream.writeByte('\n'); 
        outputStream.writeInt(Msize+HuffmanMap.size()*2);
        outputStream.writeByte('\n'); 
        outputStream.writeInt(n);
        outputStream.write('\n'); 
        outputStream.writeInt(HuffmanMap.size());
        outputStream.write('\n');
        Map.Entry<ByteArrayWrapper, String> ext = null;
        for (Map.Entry<ByteArrayWrapper, String> entry : HuffmanMap.entrySet()) {
            if(entry.getKey().getarray().length<n){
                ext = entry;
                continue;
            }
            outputStream.write(entry.getKey().getarray());
            value = (':'+entry.getValue()+'\n');
            valconverter = value.getBytes();
            outputStream.write(valconverter);
        }
        if(ext!=null){
            outputStream.write(ext.getKey().getarray());
            value = (":"+ext.getValue()+'\n');
            valconverter = value.getBytes();
            outputStream.write(valconverter);            
        }
         
        StreamReader = new BufferedInputStream(new FileInputStream(Filepath));          
        StringBuilder s = new StringBuilder();
        byte remainingBits = 0;
        List<Byte> li = new ArrayList<>();
        int size2 = (1024000/n)*n;
        byte[] newbuffer = new byte[size2];
        while ((bytesRead = StreamReader.read(newbuffer)) != -1) {
            if((bytesRead%n)==0){
                for(int i=0;i<bytesRead;i+=n){
                    ByteArrayWrapper key = new ByteArrayWrapper(Arrays.copyOfRange(buffer, i, i + n));
                    s.append(HuffmanMap.get(key));
                }
                for(int i=0;i<s.length()/8;i++){
                    for(int j=0;j<8;j++){
                        if (s.charAt(i*8+j) == '0') {
                            remainingBits <<= 1; 
                        }else{
                            remainingBits <<= 1;
                            remainingBits |= 1;  
                        }                   
                    }
                    li.add(remainingBits);
                    remainingBits = 0;
                }
                if((s.length()%8)!=0){
                    s.delete(0, (s.length()/8)*8);                    
                }else{
                    s.setLength(0);
                }
            
            }else{
                int auxsize = (bytesRead/n)*n; int x;
                for(x=0;x<auxsize;x+=n){
                    ByteArrayWrapper key = new ByteArrayWrapper(Arrays.copyOfRange(buffer, x, x + n));
                    s.append(HuffmanMap.get(key));
                }
                ByteArrayWrapper key = new ByteArrayWrapper(Arrays.copyOfRange(buffer, x, bytesRead));
                s.append(HuffmanMap.get(key));
                for(int i=0;i<s.length()/8;i++){
                    for(int j=0;j<8;j++){
                        if (s.charAt(i*8+j) == '0') {
                            remainingBits <<= 1; 
                        }else{
                            remainingBits <<= 1; 
                            remainingBits |= 1;  
                        }                   
                    }
                    li.add(remainingBits);
                    remainingBits = 0;
                }
                if((s.length()%8)!=0){
                    s.delete(0, (s.length()/8)*8);                    
                }else{
                    s.setLength(0);
                }
            }
        }
        StreamReader.close();  
        
        remainingBits=0;
        if (s.length() > 0) {
            for(int j=0;j<s.length();j++){
                if (s.charAt(j) == '0') {
                    remainingBits <<= 1; // Left shift to 0
                }else{
                    remainingBits <<= 1; // Left shift to 1
                    remainingBits |= 1;  // Set the least significant bit to 1
                }                   
            }
            remainingBits <<= (8-s.length());
            li.add(remainingBits);
        }
        
        int chunksize = 10240000, i;
        byte[] chunk;
        for(i=0;i<li.size()/chunksize;i++){
            chunk = new byte[chunksize];
            for(int j=0;j<chunksize;j++){
                chunk[j] = li.get(i*chunksize+j);
            }
            outputStream.write(chunk);
        }
        chunk = new byte[li.size()-(i*chunksize)];
        for(int l = 0 ; l<(li.size()-i*chunksize); l++){
            chunk[l] = li.get(i*chunksize+l);
        }
        if(chunk.length!=0){
            outputStream.write(chunk);
        }
        outputStream.write('\n');
        outputStream.write((byte)(s.length()));        
        outputStream.close();
        long end = System.currentTimeMillis();

        int compressedsize = li.size()+Msize+22+HuffmanMap.size()*2;
        stats inf = new stats(end-start, compressedsize, filesize);
        return inf;
    }
        
    //Decompress a given file through its path 
    public static long deCompress(String path) throws IOException{
        long start = System.currentTimeMillis();
        BufferedInputStream StreamReader = new BufferedInputStream(new FileInputStream(path));
        byte[] arr = new byte[20];
        StreamReader.read(arr);
        int originalsize =  (arr[0] & 0xFF) << 24 | (arr[1] & 0xFF) << 16 | (arr[2] & 0xFF) << 8 | (arr[3] & 0xFF);
        int headersize = (arr[5] & 0xFF) << 24 | (arr[6] & 0xFF) << 16 | (arr[7] & 0xFF) << 8 | (arr[8] & 0xFF);
        int n = (arr[10] & 0xFF) << 24 | (arr[11] & 0xFF) << 16 | (arr[12] & 0xFF) << 8 | (arr[13] & 0xFF);
        int MapSize = (arr[15] & 0xFF) << 24 | (arr[16] & 0xFF) << 16 | (arr[17] & 0xFF) << 8 | (arr[18] & 0xFF);
        
        Map<String, ByteArrayWrapper> HuffmanMap = new HashMap<>();
        byte[] header = new byte[headersize];
        StringBuilder s = new StringBuilder();
        StreamReader.read(header);
        if(originalsize%n==0){
            int i=0;
            while(i<headersize){
                ByteArrayWrapper unit = new ByteArrayWrapper(Arrays.copyOfRange(header, i, i+n));
                i+=(n+1);
                while(header[i]==48 || header[i]==49){
                    s.append((char)header[i]);
                    i++;
                }   
                HuffmanMap.put(s.toString(), unit);
                s.setLength(0);
                i++;
            }

        }else{
            int i=0, entries=0;
            while(i<headersize){
                if(entries==(MapSize-1)){
                    break;
                }

                ByteArrayWrapper unit = new ByteArrayWrapper(Arrays.copyOfRange(header, i, i+n));
                i+=(n+1);
                while(header[i]==48 || header[i]==49){
                    s.append((char)header[i]);
                    i++;
                }   
                HuffmanMap.put(s.toString(), unit);
                entries++;
                s.setLength(0);
                i++;
            }
            ByteArrayWrapper unit = new ByteArrayWrapper(Arrays.copyOfRange(header, i, i+(originalsize%n)));
            i+=((originalsize%n)+1);
            while(header[i]==48 || header[i]==49){
                s.append((char)header[i]);
                i++;
            }
            HuffmanMap.put(s.toString(), unit);
        }

        int size = 25000000;
        byte[] buffer = new byte[size];
        int bytesRead;
        StringBuilder sb = new StringBuilder();
        StringBuilder chunk = new StringBuilder();
        byte offset=0;
        List<Byte> li = new ArrayList<>();
        
        
        File newFile = new File(path);
        String NewPath = newFile.getParent()+"\\extracted."+newFile.getName().substring(0, newFile.getName().length()-3);        
        DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(NewPath));  
        byte[] chunkArray;        
        while ((bytesRead=StreamReader.read(buffer)) != -1) {
            if(bytesRead<size){
                offset = buffer[bytesRead-1];
                for(int i=0;i<bytesRead-2;i++){
                    for (int j = 7; j >= 0; j--) {
                        sb.append((buffer[i] & (1 << j)) == 0 ? '0' : '1');
                    }  
                }

            }else{
                for(byte b: buffer){
                    for (int i = 7; i >= 0; i--) {
                        sb.append((b & (1 << i)) == 0 ? '0' : '1');
                    }
                } 

            }
            if(offset!=0){
                sb.delete(sb.length()- (8-offset& 0xFF), sb.length());
            }
            int i=0;
            while(i<sb.length()){
                while(HuffmanMap.get(chunk.toString())==null&&i<sb.length()){
                    chunk.append(sb.charAt(i));
                    i++;
                }
                if(HuffmanMap.get(chunk.toString())!=null){
                    for(byte b: HuffmanMap.get(chunk.toString()).getarray()){
                        li.add(b);
                    }
                    chunk.setLength(0);
                }
            }
            sb.setLength(0);
            if(chunk.length()!=0){
                sb.append(chunk.toString());
            }
                        chunkArray = new byte[li.size()];
            for(int j=0;j<li.size();j++){
                chunkArray[j] = li.get(j);
            }
            li.clear();
            outputStream.write(chunkArray);
        }          
        outputStream.close();
        long end=System.currentTimeMillis();

        return end-start;
}

    public static void main(String[] args){
        String option = args[0];    
        String Path = args[1];
        stats info;
        long time;
            try {
                switch (option) {
                    case "d":
                        time = deCompress(Path);
                        System.out.println("DeCompression Time is "+time/1000.0+"s");                    
                        break;
                    case "c":
                        int n = Integer.parseInt(args[2]);
                        info = Compress(n, Path);
                        System.out.println("Compression Time is "+info.time/1000.0+" s"); 
                        System.out.println("Compressed file Size is "+info.compressedsize+" bytes");  
                        System.out.println("Original File Size is "+info.originalsize+" bytes");  
                        System.out.println("Compression Ratio is "+(info.compressedsize*1.0/info.originalsize)*100+" %"); 

                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }    
        }
}
