public interface IManager {
    Raid join(String name, int minPlayers) throws InterruptedException;
}
