import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Coordinator implements Runnable {
    private ServerSocket serverSocket;
    private int port;
    private boolean running;
    private Socket clientSocket;
    private List<WorkerHandler> workerHandlers;

    public Coordinator(int port) {
        this.port = port;
        this.workerHandlers = new ArrayList<>();
    }

    @Override
    public void run() {
        startListeningToNewClientConnections();

        while (running) {
            sleep();
            checkHealth();
        }

    }

    private void sleep() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkHealth() {
        for (WorkerHandler workerHandler : workerHandlers) {
            String status = null;
            try {
                status = workerHandler.pingWorker();
                logWorkerHealthStatus(workerHandler.getId(), status);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void logWorkerHealthStatus(int workerHandlerId, String status) {
        if (status.equals("working")) {
            System.out.printf("worker %d is not working!", workerHandlerId);
            System.out.println();
        } else {
            System.out.printf("worker %d is working!", workerHandlerId);
            System.out.println();
        }
    }

    private void startListeningToNewClientConnections() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    listenForNewConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void listenForNewConnection() throws IOException {
        while (running) {
            try {
                clientSocket = this.serverSocket.accept();

                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

                int id = createUid();

                WorkerHandler workerHandler = new WorkerHandler(id, clientSocket, dis, dos);

                workerHandlers.add(workerHandler);

            } catch (IOException e) {
                clientSocket.close();
                e.printStackTrace();
            }
        }
    }

    private int createUid() {
        Random rand = new Random();
        int maxNumber = 9999;
        int minNumber = 1000;

        return rand.nextInt(maxNumber) + minNumber;
    }

    public void start() {
        try {
            establishServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.running = true;

        Thread thread = new Thread(this);
        thread.start();
    }

    private void establishServer() throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.running = false;

    }


}
