package org.networks.subject6.indiviudal6.model;

import java.io.Serializable;
import java.util.Set;

public class ImageMessage implements Serializable {
    private final MessageType type;
    private final Client sender;
    private String targetId;
    private String fileName;
    private byte[] data;
    private Set<Client> activeClients;
    private String info;

    public ImageMessage(MessageType type, Client sender) {
        this.type = type;
        this.sender = sender;
    }

    public MessageType getType() {
        return type;
    }

    public Client getSender() {
        return sender;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Set<Client> getActiveClients() {
        return activeClients;
    }

    public void setActiveClients(Set<Client> activeClients) {
        this.activeClients = activeClients;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}

