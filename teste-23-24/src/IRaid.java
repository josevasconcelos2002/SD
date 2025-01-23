import java.util.List;

public interface IRaid {
    List<String> players();
    void waitStart() throws InterruptedException;
    void leave();
}
