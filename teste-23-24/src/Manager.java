import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Manager implements IManager{
    private final int R = 8;
    private Queue<Raid> playingRaids;
    ReentrantLock playingRaidsLock;
    Condition playingRaidsFull;
    private Queue<Raid> waitingRaids;
    ReentrantLock waitingRaidsLock;
    Condition groupNotFull;


    public Manager(){
        this.playingRaids = new ArrayDeque<>();
        this.playingRaidsLock = new ReentrantLock();
        this.playingRaidsFull = this.playingRaidsLock.newCondition();
        this.waitingRaids = new ArrayDeque<>();
        this.waitingRaidsLock = new ReentrantLock();
        this.groupNotFull = this.waitingRaidsLock.newCondition();
    }


    @Override
    public Raid join(String name, int minPlayers) throws InterruptedException {
        Raid result = null;
        this.waitingRaidsLock.lock();
        try{
            if(this.waitingRaids.isEmpty()){
                Raid newRaid = new Raid();
                newRaid.players().add(name);
                newRaid.incrementNrPlayers();
                if (minPlayers > newRaid.getMaxMin())
                    newRaid.setMaxMin(minPlayers);
                this.waitingRaids.add(newRaid);
                result = newRaid;


                // se newRaid ja tiver elementos suficientes, e playingRaids.size == R
                // , playingRaidsFull.await()

                // otherwise, se ja tiver elementos suficientes e plauingRaids.size < R , add Raid
                // se nao tiver elementos suficientes,

                //newRaid.waitStart();

            }
            else {
                Raid first = this.waitingRaids.element();
                if(first.isFull()){
                    int nrPlayingRaids = this.playingRaids.size();
                    if(nrPlayingRaids == R)
                        this.playingRaidsFull.await();
                    else if(nrPlayingRaids < R){
                        this.playingRaids.add(first);
                    }
                } else {
                    first.players().add(name);
                    first.incrementNrPlayers();
                    if (minPlayers > first.getMaxMin())
                        first.setMaxMin(minPlayers);
                    if(first.players().size() == first.getMaxMin())
                        first.setIsFull(true);
                    else
                        first.waitStart();
                    result = first;
                }

            }
        } finally {
            this.waitingRaidsLock.unlock();
        }
        return result;
    }
}
