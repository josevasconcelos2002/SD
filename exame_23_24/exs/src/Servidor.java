import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Servidor {



    public void main(String[] args) throws IOException {
        ServerSocket sc = new ServerSocket(1234);
        while(true){
            Socket socket = sc.accept();
            IManager manager = new Manager();
            Worker w = new Worker(socket, manager);
            Thread t = new Thread(w);
            t.start();
        }
    }


    class Worker implements Runnable{
        private Socket socket;
        private IManager manager;

        public Worker(Socket socket, IManager manager){
            this.socket = socket;
            this.manager = manager;
        }

        @Override
        public void run() {
            try{
                DataInputStream is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                DataOutputStream os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                String cmd;
                while((cmd = is.readUTF()) != null){
                    switch(cmd){
                        case "New Transfer":
                            String ID = manager.newTransfer();
                            os.writeUTF(ID);
                            os.flush();
                            break;


                        case "Send Data":
                            os.writeUTF("Transfer ID: ");
                            os.flush();
                            String id = is.readUTF();
                            ITransfer t = manager.getTransfer(id);
                            os.writeUTF("Number of Bytes to read: ");
                            os.flush();
                            int bytesToRead = is.readInt();
                            int bytesRead;
                            byte[] buffer = new byte[Transfer.transferLimit];
                            while((bytesRead = is.read(buffer)) != -1){
                                byte[] finalData = Arrays.copyOf(buffer, bytesToRead);
                                t.enqueue(finalData);
                            }
                            break;

                        case "Get Data":
                            os.writeUTF("Transfer ID: ");
                            os.flush();
                            String id1 = is.readUTF();
                            ITransfer t1 = manager.getTransfer(id1);
                            byte[] getData = t1.dequeue();
                            os.write(getData);
                            os.flush();
                            break;

                        default:
                            break;

                    }
                }

                is.close();
                os.close();
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
