public class Ship{
    int classe = 0;

    Ship(int classe){
        if(classe >= 1 && classe <= 6)
            this.classe = classe;
    }

}
