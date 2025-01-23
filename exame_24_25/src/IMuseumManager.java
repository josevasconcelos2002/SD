import java.util.Map;

public interface IMuseumManager {
    String buyTicket(int uses);
    int enterGallery(int galleryId, String ticketId) throws InterruptedException;
    void exitGallery(int galleryId, String ticketId);
    Map<Integer, Integer> peopleWaitingPerGallery();
}
