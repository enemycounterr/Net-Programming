package org.networks.subject6.subject6_2.server;

import org.networks.subject6.subject6_2.model.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SingleThreadServer {
    private static final int PORT = 5556;

    public static void main(String[] args) {
        int PORT = 5556;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.printf("Single thread server started on port %d\n", PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     ObjectInputStream is = new ObjectInputStream(clientSocket.getInputStream());
                     ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream())
                ) {
                    System.out.println("[CONN] Client connected: " + clientSocket.getRemoteSocketAddress());
                    Packet request = (Packet) is.readObject();
                    System.out.println("[RECV] Message: " + request.message());

                    String reversed = new StringBuilder(request.message()).reverse().toString();
                    Thread.sleep(5000);
                    os.writeObject(new Packet(reversed));
                    os.flush();

                    System.out.println("[SEND] Back to client reversed message");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
