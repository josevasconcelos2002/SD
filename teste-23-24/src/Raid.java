import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Raid implements IRaid{
    private List<String> players;
    private int nrPlayers;
    private int maxMin;
    private boolean isFull;
    ReentrantLock lock;


    public Raid(){
        this.players = new ArrayList<>();
        this.nrPlayers = 0;
        this.lock = new ReentrantLock();
        this.maxMin = 1;
        this.isFull = false;
    }

    public int getMaxMin(){
        return this.maxMin;
    }

    public void setMaxMin(int maxMin){
        this.maxMin = maxMin;
    }

    public void incrementNrPlayers(){
        this.nrPlayers++;
    }

    public boolean isFull(){
        return this.isFull;
    }

    public void setIsFull(boolean b){
        this.isFull = b;
    }


    @Override
    public List<String> players() {
        List<String> r = null;
        this.lock.lock();
        try{
            r = this.players;
        } finally {
            this.lock.unlock();
        }
        return r;
    }

    @Override
    public void waitStart() throws InterruptedException {

    }

    @Override
    public void leave() {

    }
}
