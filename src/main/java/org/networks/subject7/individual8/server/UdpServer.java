package org.networks.subject7.individual8.server;

import org.networks.subject7.individual8.model.FileChangeEvent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.*;

public class UdpServer {
    private static final int PORT = 9876;
    private final Set<SocketAddress> clients = ConcurrentHashMap.newKeySet();
    private final BlockingQueue<FileChangeEvent> eventQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final DatagramSocket socket;

    public UdpServer(DatagramSocket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) throws IOException {
        Path directory = Paths.get("./monitored");
        if (!directory.toFile().exists()) {
            directory.toFile().mkdirs();
        }

        DatagramSocket datagramSocket = new DatagramSocket(PORT);
        UdpServer server = new UdpServer(datagramSocket);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down server...");
            server.shutdown();
            System.out.println("Server stopped.");
        }));

        server.start(directory);

        System.out.println("Precc ctrl+c to stop the server");
    }

    public void start(Path directory) {
        executor.submit(new DirectoryMonitoring(directory, eventQueue));
        executor.submit(this::listenForClients);
        executor.submit(this::broadcastEvents);

        System.out.println("UDP Server started on port " + PORT);
    }

    private void listenForClients() {
        byte[] buffer = new byte[256];
        try {
            while (!Thread.currentThread().isInterrupted()) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                if ("SUBSCRIBE".equals(message)) {
                    clients.add(packet.getSocketAddress());
                    System.out.println("Client subscribed: " + packet.getAddress());
                }
            }
        } catch (IOException e) {
            System.err.println("Error receiving client requests: " + e.getMessage());
        }
    }

    private void broadcastEvents() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                FileChangeEvent event = eventQueue.take();
                System.out.println("Broadcasting: " + event);

                byte[] data = serializeEvent(event);
                for (SocketAddress client : clients) {
                    executor.submit(() -> sendToClient(client, data));
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void sendToClient(SocketAddress client, byte[] data) {
        try {
            DatagramPacket packet = new DatagramPacket(data, data.length, client);
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("Failed to send to " + client + ": " + e.getMessage());
        }
    }

    private byte[] serializeEvent(FileChangeEvent event) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(event);
            return bos.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    public void shutdown() {
        executor.shutdown();
        socket.close();
    }

}
