import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankTest {

    private static int INITIAL_ACCOUNTS = 100;
    private static int INITIAL_BALANCE = 1000;
    private static int WORKERS = 20;
    private static int OPS_PER_WORKER = 1000000;

    private static Bank bank;
    private static Lock lock;
    // We use a list + map to achieve O(1) inserts, removes, and random lookups
    private static List<Integer> activeIds;
    private static Map<Integer, Integer> activeIdsIndexes;
    // Stores the expected total balance
    private static int totalBalance;

    // NOTE: For simplicity, the lock in this test code is used without try/finally,
    // but in practice it should always be used.
    private static class Worker implements Runnable {
        private Random random = new Random();

        // Transfer an amount between two random accounts
        private void transfer() {
            // get two random accounts
            lock.lock();
            int from = activeIds.get(random.nextInt(activeIds.size()));
            int to = activeIds.get(random.nextInt(activeIds.size()));
            lock.unlock();

            // transfer
            bank.transfer(from, to, 1);
        }

        // Create a new account
        private void create() {
            // open a new account
            int id = bank.createAccount(INITIAL_BALANCE);

            // update the expected total balance and the active ids
            lock.lock();
            activeIds.add(id);
            activeIdsIndexes.put(id, activeIds.size() - 1);
            totalBalance += INITIAL_BALANCE;
            lock.unlock();
        }

        // Close a random account
        private void close() {
            // get a random account
            lock.lock();
            int id = activeIds.get(random.nextInt(activeIds.size()));
            lock.unlock();

            // close it
            int balance = bank.closeAccount(id);

            // update the expected total balance and the active ids
            lock.lock();
            totalBalance -= balance;
            if (activeIdsIndexes.containsKey(id)) {
                int index = activeIdsIndexes.get(id);
                int lastId = activeIds.getLast();
                activeIds.set(index, lastId);
                activeIdsIndexes.put(lastId, index);
                activeIds.removeLast();
                activeIdsIndexes.remove(id);
            }
            lock.unlock();
        }

        public void run() {
            for (int i = 0; i < OPS_PER_WORKER; i++) {
                int r = random.nextInt(100);

                // 80% probability to execute a new transfer
                if (r <= 80) {
                    transfer();
                } else { // 10% probability to create or remove an account
                    // if the number of accounts is below the initial number, open a new one
                    if (activeIds.size() < INITIAL_ACCOUNTS) {
                        create();
                    }
                    // otherwise, close one
                    else {
                        close();
                    }
                }
            }
        }
    }


    public static void main(String[] args) {
        System.out.println("Running");

        bank = new Bank();
        lock = new ReentrantLock();
        activeIds = new ArrayList<>();
        activeIdsIndexes = new HashMap<>();

        // Populate
        for (int i = 0; i < INITIAL_ACCOUNTS; i++) {
            int id = bank.createAccount(INITIAL_BALANCE);
            totalBalance += INITIAL_BALANCE;
            activeIds.add(id);
            activeIdsIndexes.put(id, i);
        }

        // Create threads
        var threads = new ArrayList<Thread>();
        for (int i = 0; i < WORKERS; i++) {
            threads.add(new Thread(new Worker()));
        }

        long start = System.nanoTime();
        // Start and join threads
        threads.forEach(Thread::start);
        threads.forEach(x -> {
            try {
                x.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        long time = System.nanoTime() - start;
        System.out.println("Time (ms): " + (time / 1e6));

        // Check if the expected total balance measured by the test threads (totalBalance)
        // matches the balance reported by the bank
        System.out.println("Expected total balance: " + totalBalance);
        System.out.println("Real total balance: " + bank.totalBalance(activeIds.stream().mapToInt(x -> x).toArray()));
    }
}
