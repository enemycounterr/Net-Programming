package org.networks.subject6.indiviudal6.client;

import org.networks.subject6.indiviudal6.model.Client;
import org.networks.subject6.indiviudal6.model.ImageMessage;
import org.networks.subject6.indiviudal6.model.MessageType;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class ImageClient {
    private static Client myProfile;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("type nick: ");
        String nick = sc.nextLine();

        try (Socket socket = new Socket("127.0.0.1", 5558);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(new ImageMessage(MessageType.REG, new Client("", nick)));

            new Thread(() -> {
                try {
                    while (true) {
                        ImageMessage msg = (ImageMessage) in.readObject();
                        switch (msg.getType()) {
                            case LIST -> {
                                myProfile = msg.getSender();
                                System.out.println("\n[ID]: " + myProfile.id() + " | Online: " + msg.getActiveClients());
                            }
                            case STATUS -> System.out.println("\n[SERVER]: " + msg.getInfo());
                            case SEND -> {
                                Files.write(new File("received_" + msg.getFileName()).toPath(), msg.getData());
                                System.out.println("\n[!] Got file from " + msg.getSender().nick());

                                ImageMessage ok = new ImageMessage(MessageType.OK, myProfile);
                                ok.setTargetId(msg.getSender().id());
                                out.writeObject(ok);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Connection closed");
                }
            }).start();

            while (true) {
                System.out.print("\n1-LIST | 2-SEND FILE | 0-EXIT: ");
                String cmd = sc.nextLine();
                if (cmd.equals("0")) break;
                if (cmd.equals("1")) out.writeObject(new ImageMessage(MessageType.REG, new Client("", nick)));
                if (cmd.equals("2")) {
                    System.out.print("recipient's ID: ");
                    String toId = sc.nextLine();
                    System.out.print("filepath: ");
                    String path = sc.nextLine();
                    Path shortPath = Path.of(path);

                    try {
                        File file = new File(shortPath.toString());
                        ImageMessage s = new ImageMessage(MessageType.SEND, myProfile);
                        s.setTargetId(toId);
                        s.setFileName(file.getName());
                        s.setData(Files.readAllBytes(file.toPath()));
                        out.writeObject(s);
                    } catch (IOException ex) {
                        System.out.println("File not found");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
