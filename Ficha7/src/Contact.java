import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

class Contact {
    private final String name;
    private final int age;
    private final long phoneNumber;
    private final String company;     // Pode ser null
    private final ArrayList<String> emails;

    public Contact(String name, int age, long phoneNumber, String company, List<String> emails) {
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.company = company;
        this.emails = new ArrayList<>(emails);
    }

    public String name() { return name; }
//    public void setName(String name){
//        this.name = name;
//    }
    public int age() { return age; }
//    public void setAge(int age){
//        this.age = age;
//    }
    public long phoneNumber() { return phoneNumber; }
//    public void setPhoneNumber(long phoneNumber){
//        this.phoneNumber = phoneNumber;
//    }
    public String company() { return company; }
//    public void setCompany(String company){
//        this.company = company;
//    }
    public ArrayList<String> emails() { return new ArrayList(emails); }
//    public void setEmails(ArrayList<String> emails){
//        this.emails = emails;
//    }


    // implentar estes metodos nas classes que queremos enviar objetos
    // @TODO
    public void serialize(DataOutputStream out) throws IOException {
        try{
            out.writeUTF(this.name());
            out.writeInt(this.age());
            out.writeLong(this.phoneNumber());
            out.writeBoolean(company() != null);
            if(company() != null)
                out.writeUTF(company());

            int length = this.emails().size();
            out.writeInt(length);

            for(String email : emails())
                out.writeUTF(email);

            // out.flush() -> ma ideia!!

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // @TODO
    public static Contact deserialize(DataInputStream in) throws IOException {
        Contact c = null;
        try{
            String name = in.readUTF();
            int age = in.readInt();
            long phoneNumber = in.readLong();

            boolean b = in.readBoolean();
            String company = null;
            if(b)
                company = in.readUTF();

            int size = in.readInt();
            ArrayList<String> emails = new ArrayList<>();
            for(int i = 0; i<size ; i++){
                emails.add(in.readUTF());
            }

            c = new Contact(name,age,phoneNumber,company,emails);

        } catch (IOException e){
            e.printStackTrace();
        }

        return c;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append(this.name).append(";");
        builder.append(this.age).append(";");
        builder.append(this.phoneNumber).append(";");
        builder.append(this.company).append(";");
        builder.append(this.emails.toString());
        builder.append("}");
        return builder.toString();
    }

}