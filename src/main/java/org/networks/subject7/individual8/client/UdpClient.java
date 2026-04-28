package org.networks.subject7.individual8.client;

import org.networks.subject7.individual8.model.FileChangeEvent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpClient {
    private static final int SERVER_PORT = 9876;
    //    private static final int CLIENT_PORT = 9877;
    private static final String SERVER_HOST = "127.0.0.1";

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            System.out.println("Client started on port: " + socket.getLocalPort());
            subscribeToServer(socket);
            listenForEvents(socket);
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }

    private static void subscribeToServer(DatagramSocket socket) throws IOException {
        byte[] subscribeMsg = "SUBSCRIBE".getBytes();
        DatagramPacket packet = new DatagramPacket(
                subscribeMsg,
                subscribeMsg.length,
                InetAddress.getByName(SERVER_HOST),
                SERVER_PORT
        );
        socket.send(packet);
        System.out.println("Subscribed to server. Waiting for file change events...");
    }

    private static void listenForEvents(DatagramSocket socket) {
        byte[] buffer = new byte[4096];

        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                FileChangeEvent event = deserializeEvent(packet.getData(), packet.getLength());
                if (event != null) {
                    System.out.println("Received: " + event);
                }
            } catch (IOException e) {
                System.err.println("Error receiving event: " + e.getMessage());
            }
        }
    }

    private static FileChangeEvent deserializeEvent(byte[] data, int length) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data, 0, length);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (FileChangeEvent) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}
