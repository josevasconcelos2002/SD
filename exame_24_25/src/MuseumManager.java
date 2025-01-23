import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MuseumManager implements IMuseumManager{
    private int N;
    private int C;
    private Integer[] galleries;
    ReentrantLock galleriesLock;
    Condition fullGallery;
    private Map<String, Integer> ticketsMap;
    ReentrantLock ticketsMapLock;
    private Map<Integer, Integer> waitingPerGallery;
    ReentrantLock waitingPerGalleryLock;

    public MuseumManager(int N, int C){
        this.N = N;
        this.C = C;
        this.galleries = new Integer[this.N];
        for(int i = 0; i<this.N ; i++){
            // ocupacao inicial de cada galeria = 0
            this.galleries[i] = 0;
        }
        this.galleriesLock = new ReentrantLock();
        this.fullGallery = this.galleriesLock.newCondition();
        this.ticketsMap = new HashMap<>();
        this.ticketsMapLock = new ReentrantLock();
        this.waitingPerGallery = new HashMap<>();

        for(int j = 0; j<this.N ; j++){
            this.waitingPerGallery.putIfAbsent(j, 0);
        }

        this.waitingPerGalleryLock = new ReentrantLock();
    }

    @Override
    public String buyTicket(int uses) {
        String ticketCode = "";
        this.ticketsMapLock.lock();
        try{
            ticketCode = UUID.randomUUID().toString();
            this.ticketsMap.put(ticketCode,uses);
        } finally {
            this.ticketsMapLock.unlock();
        }
        return ticketCode;
    }

    @Override
    public int enterGallery(int galleryId, String ticketId) throws InterruptedException {
        int result = 0;
        this.galleriesLock.lock();
        this.ticketsMapLock.lock();
        try{
            Integer entriesLeft = this.ticketsMap.get(ticketId);
            if(entriesLeft > 0){
                result = galleryId;
                if(this.galleries[galleryId] < this.C){
                    this.galleries[galleryId]++;
                } else {
                    Integer waitingPeople = this.waitingPerGallery.get(galleryId);
                    waitingPeople++;
                    this.waitingPerGallery.put(galleryId, waitingPeople);
                    this.fullGallery.await();
                }
            }
            entriesLeft--;
            this.ticketsMap.put(ticketId, entriesLeft);

        } finally {
            this.galleriesLock.unlock();
            this.ticketsMapLock.unlock();
        }
        return result;
    }

    @Override
    public void exitGallery(int galleryId, String ticketId) {
        this.galleriesLock.lock();
        try{
            this.galleries[galleryId]--;
            this.fullGallery.signal();
        } finally {
            this.galleriesLock.unlock();
        }
    }

    @Override
    public Map<Integer, Integer> peopleWaitingPerGallery() {
        return this.waitingPerGallery;
    }
}
