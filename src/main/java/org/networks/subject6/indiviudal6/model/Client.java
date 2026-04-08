package org.networks.subject6.indiviudal6.model;

import java.io.Serializable;

public record Client(String id, String nick) implements Serializable, Comparable<Client> {
    @Override
    public int compareTo(Client o) {
        return this.nick.compareToIgnoreCase(o.nick);
    }
}
