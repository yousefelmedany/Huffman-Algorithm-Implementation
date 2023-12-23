public class stats{
    long time;
    int compressedsize;
    int originalsize;     
    public stats(long time, int cmp, int original){
        this.time = time;
        this.compressedsize = cmp;
        this.originalsize = original;
    }
}
