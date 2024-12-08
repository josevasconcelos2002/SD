import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class Warehouse {
    ReentrantLock lock = new ReentrantLock();
    Condition cond = lock.newCondition();
    private Map<String, Product> map =  new HashMap<String, Product>();

    private class Product {
        int quantity = 0;
    }

    private Product get(String item) {
        Product p = map.get(item);
        if (p != null)
            return p;

        p = new Product();
        map.put(item, p);
        return p;
    }

    public void supply(String item, int quantity) {
        this.lock.lock();
        try {
            Product p = this.get(item);
            p.quantity += quantity;
            this.cond.signal();
        } finally {
            this.lock.unlock();
        }
    }

    public void consume(Set<String> items) {
        this.lock.lock();
        try {
            for (String s : items){
                Product p = this.get(s);
                if(p.quantity > 0)
                    p.quantity--;
                else
                    // esperar caso não haja stock disponivel
                    this.cond.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            this.lock.unlock();
        }
    }

}
