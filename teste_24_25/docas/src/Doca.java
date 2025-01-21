public class Doca {
    int docaID = 0;
    boolean occupied = false;

    Doca(int docaID){
        this.docaID = docaID;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public boolean isOccupied() {
        return this.occupied;
    }
}
