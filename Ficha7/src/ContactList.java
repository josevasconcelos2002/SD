import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class ContactList extends ArrayList<Contact> {

    private ArrayList<Contact> contactList;

    public ContactList(){
        this.contactList = new ArrayList<Contact>();
    }

    public ContactList(HashMap<String, Contact> contacts){
        for(Map.Entry<String,Contact> pair : contacts.entrySet() ){
            if(pair != null)
                this.contactList.add(pair.getValue());
        }

    }

    // @TODO
    public void serialize(DataOutputStream out) throws IOException {
        try{
            if(this.contactList != null) {
                int length = this.contactList.size();
                out.writeInt(length);
                for (Contact c : this.contactList)
                    c.serialize(out);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // @TODO
    public static ContactList deserialize(DataInputStream in) throws IOException {
        ContactList newContactList = new ContactList();
        try{
            int length = in.readInt();
            System.out.println(length);
            for(int i = 0; i<length ; i++){
                Contact c = Contact.deserialize(in);
                newContactList.add(c);
                System.out.println(newContactList);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return newContactList;
    }

}