import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;




public class Manager implements IManager{
    private final int docasLength = 28;
    Doca[] docas = new Doca[docasLength];
    ArrayList trips = new ArrayList<Trip>();
    ReentrantLock docasLock;
    Condition docaLivre;
    ReentrantLock tripsLock;

    Manager(){
        for(int i = 0; i<docasLength ; i++){
            this.docas[i] = new Doca(i);
        }

        ManagerRegistry.getInstance().registerManager(this);

        this.docasLock = new ReentrantLock();
        this.docaLivre = docasLock.newCondition();
        this.tripsLock = new ReentrantLock();
    }


    @Override
    public Trip permission(int size) throws InterruptedException {
        Trip trip = null;
        try {
            docasLock.lock();
            tripsLock.lock();
            Ship ship = new Ship(size);
            int d = 4 * size;
            while (trip == null) {
                for (; d < docasLength; d++) {
                    if (!this.docas[d].occupied) {
                        trip = new Trip(d, ship.classe);
                        this.docas[d].setOccupied(true);
                        this.trips.add(trip);
                        System.out.println("Barco de classe " + ship.classe + " alocado na doca " + d);
                        break;
                    }
                }
                if (trip == null) {
                    System.out.println("Esperando por uma doca livre...");
                    docaLivre.await(); // Espera até que uma doca se torne livre
                }
            }
        } finally {
            docasLock.unlock();
            tripsLock.unlock();
        }
        return trip;
    }

}
