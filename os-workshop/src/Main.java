import worker.Worker;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Coordinator coordinator = new Coordinator(8000);
        coordinator.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int n = 10;

        String className = Worker.class.getName();
        for (int i = 0; i < n; i++) {
            startWorkerProcess(className, 8000);
        }

    }

    private static void startWorkerProcess(String className, int port) {
        List<String> command = new LinkedList<>();
        command.add("java");
        command.add("-cp");
        command.add("out/production/os-workshop/");
        command.add(className);
        command.add(String.valueOf(port));

        try {
            System.out.println(command);
            ProcessBuilder builder = new ProcessBuilder(command);
            Process process = builder.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
