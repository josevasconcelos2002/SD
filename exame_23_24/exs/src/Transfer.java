import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Transfer implements ITransfer{
    private final int transferLimit = 16000;// 16k bytes
    private String ID;

    private Queue<byte[]> fileQueue;
    private int queueLength;
    private boolean transferCompleted;
    private int clientsConected;
    ReentrantLock queueLock;
    Condition queueFull;
    Condition queueEmpty;

    public Transfer(){
        this.ID = UUID.randomUUID().toString();
        this.fileQueue = new ArrayDeque<>();
        this.queueLength = 0;
        this.transferCompleted = false;
        this.clientsConected = 0;
        this.queueLock = new ReentrantLock();
        this.queueFull = this.queueLock.newCondition();
        this.queueEmpty = this.queueLock.newCondition();
    }

    public Transfer(Queue<byte[]> queue, int queueLength, boolean transferCompleted, int clientsConected){
        this.fileQueue = queue;
        this.queueLength = queueLength;
        this.transferCompleted = transferCompleted;
        this.clientsConected = clientsConected;
    }



    public String getID(){
        return this.ID;
    }

    public int getClientsConected(){
        return this.clientsConected;
    }

    public int getQueueLength(){
        return this.queueLength;
    }


    @Override
    public void enqueue(byte[] b) throws InterruptedException {
        this.queueLock.lock();
        try{
            if(b == null){
                this.transferCompleted = true;
                this.queueEmpty.signalAll();
                return;
            }

            int bytesLength = b.length;
            if(queueLength + bytesLength <= transferLimit){
                this.fileQueue.add(b);
                this.queueLength += bytesLength;
                this.queueEmpty.signal();
            } else{
                while(queueLength + bytesLength > transferLimit){
                    this.queueFull.await();
                }
            }
        } finally {
            this.queueLock.unlock();
        }
    }

    @Override
    public byte[] dequeue() throws InterruptedException {
        byte[] result = null;
        this.queueLock.lock();
        try{
            if(this.queueLength > 0){
                result = this.fileQueue.remove();
                this.queueLength -= result.length;
                this.queueFull.signal();
            } else{
                while(this.fileQueue.isEmpty()){
                    if(this.transferCompleted){
                        return null;
                    }
                    this.queueEmpty.await();
                }
            }
        } finally {
            this.queueLock.unlock();
        }
        return result;
    }
}
