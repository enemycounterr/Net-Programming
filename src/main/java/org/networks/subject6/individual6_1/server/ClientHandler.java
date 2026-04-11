package org.networks.subject6.individual6_1.server;

import org.networks.subject6.individual6_1.model.EchoPacket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }


    @Override
    public void run() {
        try (clientSocket;
             ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream is = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            EchoPacket request = (EchoPacket) is.readObject();

            long startTime = System.currentTimeMillis();

            String response = request.requestMessage() + " processed by server";
            Thread.sleep((long) (Math.random() * 50));

            long duration = System.currentTimeMillis() - startTime;

            os.writeObject(new EchoPacket(request.requestMessage(), response, duration));
            os.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
