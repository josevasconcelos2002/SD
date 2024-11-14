import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Arrays.asList;

class ContactManager {
    private Lock l = new ReentrantLock();
    private Condition con = l.newCondition();
    private HashMap<String, Contact> contacts = new HashMap<>();

    // @TODO
    public void update(Contact c) {
        try {
            l.lock();
            contacts.put(c.name(), c);
        } finally {
            l.unlock();
        }
    }

    // @TODO
    public ContactList getContacts() {
        try{
            l.lock();
            ContactList list = new ContactList();
            list.addAll(contacts.values());
            return list;
        } finally {
            l.unlock();
        }
    }
}

class ServerWorker implements Runnable {
    private Socket socket;
    private ContactManager manager;

    public ServerWorker(Socket socket, ContactManager manager) {
        this.socket = socket;
        this.manager = manager;
    }

    // @TODO
    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            ContactList contL = this.manager.getContacts();

            
            contL.serialize(out);
            out.flush();
            while (true) {
                try {
                    Contact newContact = Contact.deserialize(in);
                    this.manager.update(newContact);

                } catch (EOFException e) {
                    System.out.println("O Cliente enviou EOF!");
                    System.out.println("O Resultado das alterações é:");
                    System.out.println(this.manager.getContacts().toString());
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            socket.shutdownOutput();
            socket.shutdownInput();
            System.out.println("Conexão terminada!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



public class Server {

    public static void main (String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        ContactManager manager = new ContactManager();
        // example pre-population
        manager.update(new Contact("John", 20, 253123321, null, asList("john@mail.com")));
        manager.update(new Contact("Alice", 30, 253987654, "CompanyInc.", asList("alice.personal@mail.com", "alice.business@mail.com")));
        manager.update(new Contact("Bob", 40, 253123456, "Comp.Ld", asList("bob@mail.com", "bob.work@mail.com")));

        while (true) {
            Socket socket = serverSocket.accept();
            Thread worker = new Thread(new ServerWorker(socket, manager));
            worker.start();
        }
    }

}