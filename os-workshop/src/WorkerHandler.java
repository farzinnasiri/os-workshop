import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class WorkerHandler {
    private int id;
    private Socket clientSocket;
    private DataInputStream dis;
    private DataOutputStream dos;


    public WorkerHandler(int id, Socket clientSocket, DataInputStream dis, DataOutputStream dos) {
        this.id = id;
        this.clientSocket = clientSocket;
        this.dis = dis;
        this.dos = dos;
    }

    public String pingWorker() throws IOException {
        String request = "status";
        try {
            sendRequest(request);
            return listenForResponse();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void sendRequest(String request) throws IOException {
        dos.writeUTF(request);
    }

    private String listenForResponse() throws IOException {
        return dis.readUTF();
    }

    public int getId() {
        return this.id;
    }


}
