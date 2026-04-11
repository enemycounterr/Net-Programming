package org.networks.subject6.individual6_1.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelServer {
    private static final int PORT = 5556;

    public static void main(String[] args) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(50);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("SERVER STARTED ON PORT " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ClientHandler(clientSocket));
            }
        }
    }
}

