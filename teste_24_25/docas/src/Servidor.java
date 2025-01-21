import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    public void main(String[] args) throws IOException {
        ServerSocket sc = new ServerSocket(1234);
        IManager manager = new Manager();
        while(true){
            Socket socket = sc.accept();
            Worker worker = new Worker(socket, manager);
            Thread t = new Thread(worker);
            t.start();
        }
    }

    public class Worker implements Runnable{
        Socket socket;
        IManager manager;

        public Worker(Socket socket, IManager manager){
            this.socket = socket;
            this.manager = manager;
        }

        @Override
        public void run(){
            try{
                DataInputStream is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                DataOutputStream os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                String cmd;
                while((cmd = is.readUTF()) != null){
                    switch(cmd){
                        case "Permission":
                            os.writeUTF("Write your ship class");
                            os.flush();
                            Integer classe = is.readInt();
                            Trip trip = manager.permission(classe);

                        case "Disembark":
                            trip.waitDisembark();

                    }
                }


                is.close();
                os.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
