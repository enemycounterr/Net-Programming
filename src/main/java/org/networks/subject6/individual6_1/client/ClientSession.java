package org.networks.subject6.individual6_1.client;

import org.networks.subject6.individual6_1.model.EchoPacket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientSession implements Runnable {
    private final String HOST;
    private final int PORT;
    private final AtomicInteger COUNTER;

    public ClientSession(String host, int port, AtomicInteger counter) {
        this.HOST = host;
        this.PORT = port;
        this.COUNTER = counter;
    }

    @Override
    public void run() {
        String data = UUID.randomUUID().toString().substring(0, 8);
        try (Socket socket = new Socket(HOST, PORT);
             ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream is = new ObjectInputStream(socket.getInputStream())
        ) {
            os.writeObject(new EchoPacket(data, null, 0));
            os.flush();

            EchoPacket response = (EchoPacket) is.readObject();
            int total = this.COUNTER.incrementAndGet();
            if (total % 100 == 0) {
                System.out.println("Progress: " + total + "/1000. Last: " + response.responseMessage() + " /Duration: " + response.durations());
            }

    
        } catch (Exception e) {
            System.out.println("Error session: " + e.getMessage());
        }
    }
}
