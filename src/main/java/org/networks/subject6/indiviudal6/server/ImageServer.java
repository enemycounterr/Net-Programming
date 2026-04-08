package org.networks.subject6.indiviudal6.server;

import org.networks.subject6.indiviudal6.model.Client;
import org.networks.subject6.indiviudal6.model.ClientStorage;
import org.networks.subject6.indiviudal6.model.ImageMessage;
import org.networks.subject6.indiviudal6.model.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ImageServer {
    private static final Map<String, ObjectOutputStream> streams = new ConcurrentHashMap<>();
    private static final ClientStorage storage = new ClientStorage();

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(5558)) {
            System.out.println("Routing Server Started...");
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handle(socket)).start();
            }
        }
    }

    private static void handle(Socket socket) {
        Client myInfo = null;
        try (socket;
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

//            Client myInfo = null;

            while (true) {
                ImageMessage msg = (ImageMessage) in.readObject();
                switch (msg.getType()) {
                    case REG -> {
                        if (myInfo != null) {
                            System.out.println("[LIST] Updating list for: " + myInfo.nick());
                        } else {
                            String id = UUID.randomUUID().toString().substring(0, 4);
                            myInfo = new Client(id, msg.getSender().nick());
                            storage.add(myInfo);
                            streams.put(id, out);
                            System.out.println("[REG] Registered " + myInfo.nick());
                        }
                        ImageMessage resp = new ImageMessage(MessageType.LIST, myInfo);
                        resp.setActiveClients(storage.getAll());

                        out.reset();
                        out.writeObject(resp);
                        out.flush();
                    }
                    case SEND -> {
                        ObjectOutputStream targetOut = streams.get(msg.getTargetId());
                        if (targetOut != null) {
                            targetOut.writeObject(msg);
                        } else {
                            ImageMessage err = new ImageMessage(MessageType.STATUS, new Client("0", "System"));
                            err.setInfo("Undelivered: Client " + msg.getTargetId() + " not found");
                            out.writeObject(err);
                        }
                    }
                    case OK -> {
                        ObjectOutputStream senderOut = streams.get(msg.getTargetId());
                        if (senderOut != null) {
                            ImageMessage status = new ImageMessage(MessageType.STATUS, msg.getSender());
                            status.setInfo("Message delivered to " + msg.getSender().nick());
                            senderOut.writeObject(status);
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (myInfo != null) {
                System.out.printf("[EXIT] Client %s closed connection\n", myInfo.nick());
                storage.remove(myInfo);
                streams.remove(myInfo.id());

            }
        }
    }

}
