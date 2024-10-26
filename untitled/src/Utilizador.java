import java.util.Objects;
import java.util.UUID;

public class Utilizador {
    private String name;
    private final String password;

    Utilizador() {
        this.name = "";
        this.password = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getPassword(){
        return this.password;
    }

    public boolean validatePassword(String password){
        return Objects.equals(password, this.password);
    }
}
