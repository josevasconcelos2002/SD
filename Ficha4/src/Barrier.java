import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class Barrier {
    private final int N;
    private int count = 0;
    ReentrantLock lock = new ReentrantLock();
    Condition cond = lock.newCondition();

    Barrier (int N) {
        this.N = N;
    }
    void await() throws InterruptedException {
        this.lock.lock();
        try{
            //System.out.println("N: " + N + "\n");
            count++;
            //System.out.println("Count: " + count + "\n");
            while(N > count){
                this.cond.await();
            }
            this.cond.signalAll();
        } finally {
            this.lock.unlock();
        }
    }

    public static void main(String[] args) {
        int N = 10;
        Barrier b = new Barrier(N);

        for(int i = 1; i<=N ; i++){
            int threadID = i;
            new Thread(() -> {
                System.out.println("Thread " + threadID + " is running its code.");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try{
                    b.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Thread " + threadID + " passed the barrier");
            }).start();
        }
    }
}
