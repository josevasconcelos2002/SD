import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;


class Register{
    private int sum = 0;
    private int count = 0;
    private ReentrantLock lock = new ReentrantLock();

    public void add(int val){
        lock.lock();
        try {
            sum += val;
            count++;
        } finally {
            lock.unlock();
        }
    }

    public int average(){
        lock.lock();
        try {
            return count > 0 ? sum / count : 0;
        }
        finally {
            lock.unlock();
        }
    }
}

class ClientHandler extends Thread implements Runnable{
    private  Socket socket;
    private  Register register;

    public ClientHandler(Register register, Socket socket){
        this.register = register;
        this.socket = socket;
    }

    public void run() {
        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream()); // escrever no socket

            String line;
            int sum = 0;
            while ((line = in.readLine()) != null) {
                int num = Integer.parseInt(line);
                sum += num;
                register.add(num);
                out.println(Integer.toString(sum));
                out.flush();  // flush manual!  -> escrever no socket e enviar -> evita deadlock
            }
            
            socket.shutdownInput();

            out.println(register.average());
            out.flush();

            socket.shutdownOutput();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}


public class EchoServer{

    public static void main(String[] args) {

        try {
        ServerSocket ss = new ServerSocket(12345); // listening socket
            Register register = new Register();
            while (true) {

                Socket socket = ss.accept(); // connected socket
                Thread newTread = new Thread(new ClientHandler(register,socket));
                newTread.start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}