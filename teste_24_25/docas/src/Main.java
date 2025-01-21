import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        // Criar o ManagerRegistry e o Manager
        ManagerRegistry registry = ManagerRegistry.getInstance();
        Manager manager = new Manager();
        registry.registerManager(manager);

        // Criar um ExecutorService para simular chamadas concorrentes ao método permission
        ExecutorService executor = Executors.newFixedThreadPool(7); // 5 threads para concorrência

        // Simulando várias chamadas ao método permission com diferentes tamanhos de barcos (ex: tamanhos de 1 a 5)
        for (int i = 0; i < 8; i++) {
            final int size = i + 1; // Tamanho de barco vai de 1 a 5
            executor.submit(() -> {
                try {
                    System.out.println("Requisitando permissão para um barco de classe " + size);
                    Trip trip = manager.permission(size);
                    System.out.println("Barco de classe " + size + " alocado na doca " + trip.dockId());

                    // Simula o processo de desembarque e partida
                    trip.waitDisembark(); // Espera o desembarque
                    trip.finishedDisembark(); // Finaliza o desembarque
                    trip.depart(); // O barco parte da doca

                    // Verificar se a doca foi desocupada corretamente
                    if (!manager.docas[trip.dockId()].isOccupied()) {
                        System.out.println("A doca " + trip.dockId() + " foi desocupada corretamente.");
                    } else {
                        System.out.println("Erro: a doca " + trip.dockId() + " ainda está ocupada.");
                    }

                    System.out.println("Barco de classe " + size + " partiu da doca " + trip.dockId());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // Aguardar a conclusão de todas as threads
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("Todas as viagens foram processadas.");
    }
}
