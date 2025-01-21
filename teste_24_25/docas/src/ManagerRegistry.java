public class ManagerRegistry {
    // Instância única da classe (Singleton)
    private static ManagerRegistry instance;
    private Manager manager;

    // Construtor privado para evitar que outras classes criem uma instância diretamente
    private ManagerRegistry() {}

    // Método para obter a instância única do ManagerRegistry
    public static synchronized ManagerRegistry getInstance() {
        if (instance == null) {
            instance = new ManagerRegistry();
        }
        return instance;
    }

    // Registrar a instância do Manager
    public void registerManager(Manager manager) {
        this.manager = manager;
    }

    // Obter a instância do Manager registrada
    public Manager getManager() {
        if (this.manager == null) {
            throw new IllegalStateException("Manager ainda não foi registrado!");
        }
        return this.manager;
    }
}
