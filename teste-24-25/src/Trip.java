import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Trip implements TripManager{
    private int dockID;
    private int shipClass;
    private boolean disembarked;
    private ManagerRegistry managerRegistry;
    ReentrantLock tripLock;
    Condition disembark;


    Trip(int dockID, int shipClass){
        if (dockID < 0 || dockID >= 28) {
            throw new IllegalArgumentException("Invalid dock ID");
        }
        this.dockID = dockID;
        this.shipClass = shipClass;
        this.disembarked = false;
        this.tripLock = new ReentrantLock();
        this.disembark = tripLock.newCondition();
    }

    @Override
    public int dockId(){
        return this.dockID;
    }

    @Override
    public void waitDisembark() throws InterruptedException {
        this.tripLock.lock();
        try {
            while(!this.disembarked){
                this.disembark.await();
            }
        } finally {
            this.tripLock.unlock();
        }
    }

    @Override
    public void finishedDisembark() {
        this.tripLock.lock();
        try {
            this.disembarked = true;
            this.disembark.signal();
        } finally {
            this.tripLock.unlock();
        }
    }

    @Override
    public void depart() {
        Manager manager = ManagerRegistry.getInstance().getManager();
        try {
            manager.docasLock.lock();
            manager.tripsLock.lock();

            // Libera a doca e remove a viagem da lista
            manager.docas[this.dockID].setOccupied(false);
            manager.trips.remove(this);

            // Sinaliza que uma doca ficou livre
            System.out.println("Barco de classe " + this.shipClass + " partiu da doca " + this.dockID);
            manager.docaLivre.signal(); // Sinaliza que uma doca está agora livre

        } finally {
            manager.docasLock.unlock();
            manager.tripsLock.unlock();
        }
    }

}
