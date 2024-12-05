import java.util.concurrent.locks.ReentrantLock;

class Bank implements Runnable {
    Account acc;
    ReentrantLock lock = new ReentrantLock();

    Bank(Account acc){
        this.acc = acc;
    }

    public void run() {
        final int I = 1000;
        final int V = 100;

        for (int i = 0; i < I; i++){
            try{
                lock.lock();
                this.acc.deposit(V);
                System.out.println(this.acc.balance());
            } finally {
                lock.unlock();
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        int N = 100;
        Bank bank = new Bank(new Account(0));
        for(int i = 0; i<N; i++){
            Thread t = new Thread(bank);
            t.start();
            t.join(); // espera que a Thread termine
        }
    }

    private static class Account {
        private int balance;

        Account(int balance) {
            this.balance = balance;
        }

        int balance() {
            return balance;
        }

        boolean deposit(int value) {
            balance += value;
            return true;
        }
    }

    // Our single account, for now
    private Account savings = new Account(0);

    // Account balance
    public int balance() {
        return savings.balance();
    }

    // Deposit
    boolean deposit(int value) {
        return savings.deposit(value);
    }
}