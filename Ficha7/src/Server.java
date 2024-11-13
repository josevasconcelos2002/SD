import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Arrays.asList;

class ContactManager {
    private HashMap<String, Contact> contacts = new HashMap<>();
    private ContactList contactsList = new ContactList(this.contacts);
    private ReentrantLock lock = new ReentrantLock();

    public HashMap<String, Contact> getContacts(){
        return this.contacts;
    }

    public ContactList getContactList(){
        return this.contactsList;
    }


    // @TODO
    public void update(Contact c) {

        try {
            lock.lock();
            Contact c1 = this.contacts.get(c.name());
            if (c1 != null) {
                c1.setName(c.name());
                c1.setAge(c.age());
                c1.setPhoneNumber(c.phoneNumber());
                c1.setCompany(c.company());
                c1.setEmails(c.emails());
                System.out.println(c1);
            } else {
                this.contacts.put(c.name(), c);
                boolean c2 = this.contactsList.add(c);
                //boolean c3 = this.contactsList.contains(c);
                //Contact c2 = this.contacts.get(c.name());
                //System.out.println(c2);
                System.out.println("\n" + this.getContactList());
            }
        } finally {
            lock.unlock();
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
        try{
            DataInputStream in = new DataInputStream(this.socket.getInputStream());
            DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());

            this.manager.getContactList().serialize(out);
            out.flush();

            Contact newContact = Contact.deserialize(in);

            this.manager.update(newContact);

            System.out.println(this.manager.getContacts() + "\n");


        } catch (Exception e) {
             e.printStackTrace();
        }
    }
}



public class Server {

    public static void main (String[] args) throws IOException {
        try{
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
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}