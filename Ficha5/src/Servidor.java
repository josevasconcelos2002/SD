import java.util.HashSet;
import java.util.Set;

public class Servidor {
    public static void main(String[] args) {
        Warehouse w = new Warehouse();

        new Thread(()-> {
                    System.out.printf("Vou fazer supply na T=%d\n", 1);
                    w.supply("Leite", 10);
                    w.supply("Donuts", 5);
                    System.out.printf("Supply Feito na T=%d\n", 1);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        for(int i = 0; i<5; i++){
            int fI = i;
            new Thread(() -> {
                while(true) {
                    try {
                        Set<String> items = new HashSet<>();
                        items.add("Leite");
                        items.add("Donuts");
                        w.consume(items);

                        System.out.printf("Consumidor consumiu na T=%d\n", fI);

                    } catch (Exception e) {}
                    System.out.printf("await retornou (T=%d)\n", 1);
                }

            }).start();
        }
    }
}
