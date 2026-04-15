package org.networks.subject6.subject6_2.client;

import org.networks.subject6.subject6_2.model.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SingleThreadClient {
    private static final int PORT = 5556;

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter message to server:");
        String text = sc.nextLine();

        try (Socket socket = new Socket("127.0.0.1", PORT);
             ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream is = new ObjectInputStream(socket.getInputStream())
        ) {
            os.writeObject(new Packet(text));
            os.flush();
            System.out.println("Message sent to the server");

            Packet response = (Packet) is.readObject();
            System.out.println("Response from server: " + response.message());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
