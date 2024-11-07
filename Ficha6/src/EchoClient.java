import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClient {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream()); // imprimir para o socket

            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

            String userInput;
            while ((userInput = systemIn.readLine()) != null) { // Ctrl + D -> EOF
                try{
                    int num = Integer.parseInt(userInput);
                    out.println(userInput);
                    out.flush();

                    String response = in.readLine();
                    System.out.println("Soma:  " + response);

                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid integer.");
                }

            }

            socket.shutdownOutput(); // EOF para o servidor
            String finalAverage = in.readLine();
            System.out.println("Final Average: " + finalAverage);

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}