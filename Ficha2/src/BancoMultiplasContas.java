import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BancoMultiplasContas {


    private static class Account {
        private int balance;
        private ReentrantLock lock = new ReentrantLock();

        Account (int balance) { this.balance = balance; }

        int balance () { return balance; }

        boolean deposit (int value) {
            this.lock.lock();
            try {
                balance += value;
                return true;
            } finally {
                this.lock.unlock();
            }
        }

        boolean withdraw (int value) {
            this.lock.lock();
            try {
                if (value > balance)
                    return false;
                balance -= value;
                return true;
            } finally {
                this.lock.unlock();
            }
        }
    }

    // Bank slots and vector of accounts
    private final int slots;
    private Account[] av;
    private ReentrantLock lock = new ReentrantLock();

    public BancoMultiplasContas (int n) {
        slots=n;
        av=new Account[slots];
        for (int i=0; i<slots; i++)
            av[i]=new Account(0);
    }


    // Account balance
    public int balance (int id) {
        this.lock.lock();
        try {
            if (id < 0 || id >= slots)
                return 0;
            return av[id].balance();
        } finally {
            this.lock.unlock();
        }
    }

    // Deposit
    public boolean deposit (int id, int value) {
        this.lock.lock();
        try {
            if (id < 0 || id >= slots)
                return false;
            return av[id].deposit(value);
        } finally {
            this.lock.unlock();
        }
    }

    // Withdraw; fails if no such account or insufficient balance
    public boolean withdraw (int id, int value) {
        this.lock.lock();
        try {
            if (id < 0 || id >= slots)
                return false;
            return av[id].withdraw(value);
        } finally {
            this.lock.unlock();
        }
    }

    // Transfer
    public boolean transfer (int from, int to, int value) {
        boolean result = false;
        if (from < 0 || from >= slots || to < 0 || to >= slots){
            return result;
        }
        Account fromAccount = av[from];
        Account toAccount = av[to];

        if(from < to){
            fromAccount.lock.lock();
            toAccount.lock.lock();
        } else {
            toAccount.lock.lock();
            fromAccount.lock.lock();
        }
        try{
            if(fromAccount.withdraw(value) && toAccount.deposit(value))
                result = true;
        } finally {
            toAccount.lock.unlock();
            fromAccount.lock.unlock();
        }
        return result;
    }

    // TotalBalance
    public int totalBalance () {
        int total = 0;
        this.lock.lock();
        try{
            for(Account ac : this.av) {
                total += ac.balance();
            }
        } finally {
            this.lock.unlock();
        }
        return total;
    }
}

