import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Manager implements IManager{
    private final int serverLimit = 16000000; //16M bytes
    private Map<String, Transfer> transfers;
    private Queue<Transfer> pendingTransfers;
    private int serverLength;
    ReentrantLock transfersLock;
    Condition serverFull;


    public Manager(){
        this.transfers = new HashMap<>();
        this.pendingTransfers = new ArrayDeque<>();
        this.serverLength = 0;
        this.transfersLock = new ReentrantLock();
        this.serverFull = this.transfersLock.newCondition();
    }




    @Override
    public String newTransfer() {
        String ID = "";
        this.transfersLock.lock();
        try{
            Transfer t = new Transfer();
            if(this.serverLength + t.getQueueLength() <= serverLimit){
                this.transfers.put(t.getID(), t);
                ID = t.getID();
            } else {
                while(this.serverLength + t.getQueueLength() > serverLimit) {
                    this.pendingTransfers.add(t);
                    this.serverFull.await();
                }
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            this.transfersLock.unlock();
        }
        return ID;
    }

    @Override
    public Transfer getTransfer(String identifier) {
        Transfer result = null;
        this.transfersLock.lock();
        try{
            Transfer transfer = this.transfers.get(identifier);
            if(transfer.getClientsConected() == 2){
                throw new Exception("Numero maximo de clientes ja alcancado!");
            } else {
                result = transfer;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.transfersLock.unlock();
        }
        return result;
    }
}
