package g8;

import java.net.Socket;

public class SimpleClient {
    public static void main(String[] args) throws Exception {
        try (Socket s = new Socket("localhost", 12345);
             FramedConnection c = new FramedConnection(s)) {

            // Enviar uma mensagem
            c.send("Ola".getBytes());

            // Receber a resposta
            byte[] b1 = c.receive();
            System.out.println("Some Reply: " + new String(b1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
