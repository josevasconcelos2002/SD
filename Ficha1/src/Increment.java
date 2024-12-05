class Increment implements Runnable {
    public void run() {
        final long I = 100;

        for (long i = 0; i < I; i++)
            System.out.println(i);
    }

    public static void main(String[] args) throws InterruptedException {
        int N = 10;
        for(int i = 0; i<N; i++){
            Thread t = new Thread(new Increment());
            t.start();
            t.join(); // espera que a Thread termine
        }
    }
}