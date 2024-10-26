import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Servidor {

    private Map<String, Utilizador> utilizadores;

    public Servidor() {
        this.utilizadores = new ConcurrentHashMap<>();
    }

    public boolean adicionarUtilizador(Utilizador utilizador){
        if(this.utilizadores.containsKey(utilizador.getName()))
            return false;
        try {
            this.utilizadores.put(utilizador.getName(), utilizador);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean autenticarUtilizador(Utilizador utilizador) {
        if (!this.utilizadores.containsKey(utilizador.getName())) {
            return false;
        }
        try {
            return utilizador.validatePassword(utilizador.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
