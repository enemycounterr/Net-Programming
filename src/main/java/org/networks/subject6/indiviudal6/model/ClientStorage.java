package org.networks.subject6.indiviudal6.model;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class ClientStorage {
    private final Set<Client> clients = new ConcurrentSkipListSet<>();

    public void add(Client client) {
        clients.add(client);
    }

    public void remove(Client client) {
        clients.remove(client);
    }

    public Set<Client> getAll() {
        return Collections.unmodifiableSet(clients);
    }
}
