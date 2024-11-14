package g8;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class FramedConnection implements AutoCloseable {
    private Socket socket;
    private ReentrantLock l = new ReentrantLock();

    public FramedConnection(Socket socket) {
        this.socket = socket;
    }

    public void send(byte[] data) throws IOException {
        try {
            l.lock();
            DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
            out.writeInt(data.length);  // Envia o comprimento como um int de 4 bytes
            out.write(data);            // Envia os dados
        } finally {
            l.unlock();
        }
    }

    public byte[] receive() throws IOException {
        try {
            l.lock();
            DataInputStream in = new DataInputStream(this.socket.getInputStream());

            int length = in.readInt();  // Lê o comprimento de 4 bytes
            System.out.println(length);
            byte[] data = new byte[length];
            in.readFully(data);         // Lê os dados completos
            System.out.println(Arrays.toString(data));

            return data;
        } finally {
            l.unlock();
        }
    }

    public void close() throws IOException {
        this.socket.close();
    }
}
