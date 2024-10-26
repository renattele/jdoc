package jdoc.domain;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HostManager {
    private final Host host;
    private final Gson gson;
    private final Map<String, String> clients = new HashMap<>();

    public HostManager(Host host, Gson gson) {
        this.host = host;
        host.messages().subscribe(message -> {
            if (message.type() == Message.SET_USERNAME) {
                setClientName(message);
            }
        });
        host.clients().subscribe(clientsAddr -> {
            var clientsAddrSet = new HashSet<>(clientsAddr);
            syncClientNames(clientsAddrSet);
            broadcastClientNames();
        });
        this.gson = gson;
    }

    private synchronized void syncClientNames(Set<String> clientsAddrSet) {
        for (String clientAddr : clientsAddrSet) {
            clients.putIfAbsent(clientAddr, clientAddr);
        }
        for (String clientAddr : clients.keySet()) {
            if (!clientsAddrSet.contains(clientAddr)) {
                clients.remove(clientAddr);
            }
        }
    }

    private void setClientName(Message message) {
        String name;
        if (message.dataString().isEmpty()) {
            name = message.sender();
        } else {
            name = message.dataString();
        }
        clients.put(message.sender(), name);
        broadcastClientNames();
    }

    private void broadcastClientNames() {
        var clientNames = gson.toJson(clients.values());
        var broadcastMessage = new Message(Message.GET_CLIENTS, clientNames);
        host.broadcast(broadcastMessage);
    }
}
