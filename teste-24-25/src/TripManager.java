public interface TripManager {
    int dockId();
    void waitDisembark() throws InterruptedException;
    void finishedDisembark();
    void depart();
}
