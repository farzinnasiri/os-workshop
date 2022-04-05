package worker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class Worker {

    private boolean working;
    private boolean running;
    private int port;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public Worker(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("not enough arguments");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

//        int port = 8000;
        Worker worker = new Worker(port);
        try {
            worker.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void start() throws IOException {
        running = true;

        establishConnection();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    listen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        work();

    }

    private void establishConnection() throws IOException {
        socket = new Socket("localhost", port);

        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }

    private void listen() throws IOException {
        while (running) {
            String message = dis.readUTF();
            handleMessage(message);
        }
    }

    private void handleMessage(String message) throws IOException {
        if (message.equals("status")) {
            dos.writeUTF(getStatus());
        }
    }

    private void work() {
        working = true;
        int count = 1;
        while (working) {
            count += 1;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            checkCounter(count);
        }
    }

    private void checkCounter(int count) {
        if (count < 5) {
            return;
        }

        Random random = new Random();
        if (random.nextInt(8) == 1) {
            working = false;
        }
    }

    private String getStatus() {
        if (working) return "working";
        return "not working";
    }

}
