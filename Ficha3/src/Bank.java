import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

class Bank {

    private static class Account {
        private int balance;
        ReentrantLock lock = new ReentrantLock();
        Account(int balance) { this.balance = balance; }
        int balance() { return balance; }
        boolean deposit(int value) {
            balance += value;
            return true;
        }
        boolean withdraw(int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }
    }

    private Map<Integer, Account> map = new HashMap<Integer, Account>();
    private int nextId = 0;
    ReentrantLock lock = new ReentrantLock();

    // create account and return account id
    public int createAccount(int balance) {
        this.lock.lock();
        try{
            Account c = new Account(balance);
            int id = nextId;
            nextId += 1;
            map.put(id, c);
            return id;
        } finally {
            this.lock.unlock();
        }
    }

    // close account and return balance, or 0 if no such account
    public int closeAccount(int id) {
        lock.lock();
        try {
            Account c = map.remove(id);
            if (c == null)
                return 0;
            c.lock.lock();
            try {
                return c.balance();
            } finally {
                c.lock.unlock();
            }
        } finally {
            lock.unlock();
        }
    }

    // account balance; 0 if no such account
    public int balance(int id) {
        lock.lock();
        try {
            Account c = map.get(id);
            if (c == null)
                return 0;
            c.lock.lock();
            try {
                return c.balance();
            } finally {
                c.lock.unlock();
            }
        } finally {
            lock.unlock();
        }
    }

    // deposit; fails if no such account
    public boolean deposit(int id, int value) {
        lock.lock();
        try {
            Account c = map.get(id);
            if (c == null)
                return false;
            c.lock.lock();
            try {
                return c.deposit(value);
            } finally {
                c.lock.unlock();
            }
        } finally {
            lock.unlock();
        }
    }

    // withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        lock.lock();
        try {
            Account c = map.get(id);
            if (c == null)
                return false;
            c.lock.lock();
            try {
                return c.withdraw(value);
            } finally {
                c.lock.unlock();
            }
        } finally {
            lock.unlock();
        }
    }

    // transfer value between accounts;
    // fails if either account does not exist or insufficient balance
    public boolean transfer(int from, int to, int value) {
        lock.lock();
        try {
            Account cfrom, cto;
            cfrom = map.get(from);
            cto = map.get(to);
            if (cfrom == null || cto == null)
                return false;
            cfrom.lock.lock();
            cto.lock.lock();
            try {
                return cfrom.withdraw(value) && cto.deposit(value);
            } finally {
                cfrom.lock.unlock();
                cto.lock.unlock();
            }
        } finally {
            lock.unlock();
        }
    }

    // sum of balances in set of accounts; 0 if some does not exist
    public int totalBalance(int[] ids) {
        lock.lock();
        try {
            int total = 0;
            Account[] account_locked = new Account[this.map.size()];
            for(int j = 0; j<ids.length ; j++)
                account_locked[j] = this.map.get(ids[j]);
            for(Account ac : account_locked)
                ac.lock.lock();
            for (Account c : account_locked) {
                if (c == null)
                    return 0;
                total += c.balance();
                c.lock.unlock();
            }
            return total;
        } finally {
            lock.unlock();
        }
    }

}
