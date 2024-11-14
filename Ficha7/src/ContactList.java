import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class ContactList extends ArrayList<Contact> {


    // @TODO
    public void serialize(DataOutputStream out) throws IOException {
        int length = size();
        out.writeInt(length);
        for (Contact c : this){
            c.serialize(out);
        }
    }

    // @TODO
    public static ContactList deserialize(DataInputStream in) throws IOException {
        int length = in.readInt();
        ContactList list = new ContactList();
        for(int i = 0; i<length ; i++){
            Contact c = Contact.deserialize(in);
            list.add(c);
        }
        return list;
    }


public void main() {
}

}
