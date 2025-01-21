import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ex1 {

    class Manager implements ManagerI{
        ReentrantLock lock = new ReentrantLock();
        Condition cond = lock.newCondition();
        private Raid current = new Raid();
        private int maxMin = 0;
        private Queue<Raid> waitingQueue = new ArrayDeque<>();
        @Override
        public Raid join(String name, int minPlayers) throws InterruptedException {
            this.lock.lock();
            try{
                this.current.players.add(name);
            } finally {
              this.lock.unlock();
                }

            
                }

    }

    class Raid implements RaidI{
        private List<String> players;

        Raid(){
            this.players = new ArrayList<>();
        }

    }

    interface ManagerI {
        Raid join(String name, int minPlayers) throws InterruptedException;
    }
    interface RaidI {
        List<String> players();
        void waitStart() throws InterruptedException;
        void leave();
    }

}
