import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Client {

    public static Contact parseLine(String userInput) {
        String[] tokens = userInput.split(" ");

        if (tokens[3].equals("null")) tokens[3] = null;

        return new Contact(
                tokens[0],
                Integer.parseInt(tokens[1]),
                Long.parseLong(tokens[2]),
                tokens[3],
                new ArrayList<>(Arrays.asList(tokens).subList(4, tokens.length)));
    }


    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);

        DataInputStream in = new DataInputStream(socket.getInputStream());
        BufferedReader inBuff = new BufferedReader(new InputStreamReader(System.in));
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        ContactList ct = ContactList.deserialize(in);
        System.out.println(ct.toString());

        String userInput;
        while ((userInput = inBuff.readLine()) != null) {
            Contact newContact = parseLine(userInput);
            System.out.println(newContact.toString());
            newContact.serialize(out);
            out.flush();
        }
        socket.close();
    }
}