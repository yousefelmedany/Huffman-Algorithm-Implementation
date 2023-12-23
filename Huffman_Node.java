public class Huffman_Node{
    byte[] data;
    int frequency;
    String codelength;
    Huffman_Node leftChild;
    Huffman_Node rightChild;

    public Huffman_Node(byte[] d, int f, String c, Huffman_Node l, Huffman_Node r){
            this.data=d;
            this.codelength=c;
            this.frequency = f;
            this.leftChild = l;
            this.rightChild = r;
    }
}