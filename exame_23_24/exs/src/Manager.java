import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Manager implements IManager{
    private final int serverLimit = 16000000; //16M bytes
    private Map<String, Transfer> transfers;
    private int serverLength;
    ReentrantLock transfersLock;
    Condition serverFull;


    public Manager(){
        this.transfers = new HashMap<>();
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
            } else {
                while(this.serverLength + t.getQueueLength() > serverLimit)
                    this.serverFull.await();
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
