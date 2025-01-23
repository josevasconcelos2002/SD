import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public void main(String[] args) throws IOException {
        ServerSocket sc = new ServerSocket(1234);
        IMuseumManager manager = new MuseumManager(10,100);
        while(true){
            Socket socket = sc.accept();
            Worker w = new Worker(socket,manager);
            Thread t = new Thread(w);
            t.start();
        }
    }

    class Worker implements Runnable{
        private Socket socket;
        private IMuseumManager manager;

        public Worker(Socket socket, IMuseumManager manager){
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
                       case "Buy Ticket":
                           os.writeUTF("Number of uses: ");
                           os.flush();
                           int uses = is.readInt();
                           String ticketCode = manager.buyTicket(uses);
                           os.writeUTF(ticketCode);
                           os.flush();


                       case "Enter Gallery":
                           os.writeUTF("Enter the gallery id: ");
                           os.flush();
                           int galleryID1 = is.readInt();
                           os.writeUTF("Enter your ticket code: ");
                           os.flush();
                           String code1 = is.readUTF();
                           int gallery = manager.enterGallery(galleryID1,code1);
                           os.writeInt(gallery);
                           os.flush();

                       case "Exit Gallery":
                           os.writeUTF("Enter the gallery id: ");
                           os.flush();
                           int galleryID = is.readInt();
                           os.writeUTF("Enter your ticket code: ");
                           os.flush();
                           String code = is.readUTF();
                           manager.exitGallery(galleryID,code);
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
