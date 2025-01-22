public interface ITransfer {
    void enqueue(byte[] b) throws InterruptedException;
    byte[] dequeue() throws InterruptedException;
}
