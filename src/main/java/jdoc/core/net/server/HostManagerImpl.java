package jdoc.core.net.server;

import jdoc.core.domain.Serializer;
import jdoc.core.net.protocol.Message;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HostManagerImpl {
    private final ServerConnection serverConnection;
    private final Serializer serializer;
    private final Map<String, String> clients = new HashMap<>();

    public HostManagerImpl(ServerConnection serverConnection, Serializer serializer) {
        this.serverConnection = serverConnection;
        serverConnection.messages().subscribe(message -> {
            if (message.type() == Message.SET_USERNAME) {
                setClientName(message);
            }
        });
        serverConnection.clients().subscribe(clientsAddr -> {
            var clientsAddrSet = new HashSet<>(clientsAddr);
            syncClientNames(clientsAddrSet);
            broadcastClientNames();
        });
        this.serializer = serializer;
    }

    public synchronized void syncClientNames(Set<String> clientsAddrSet) {
        for (String clientAddr : clientsAddrSet) {
            clients.putIfAbsent(clientAddr, clientAddr);
        }
        for (String clientAddr : clients.keySet()) {
            if (!clientsAddrSet.contains(clientAddr)) {
                clients.remove(clientAddr);
            }
        }
    }

    public void setClientName(Message message) {
        String name;
        if (message.dataString().isEmpty()) {
            name = message.sender();
        } else {
            name = message.dataString();
        }
        clients.put(message.sender(), name);
        broadcastClientNames();
    }

    public void broadcastClientNames() {
        var clientNames = serializer.toString(clients.values());
        var broadcastMessage = new Message(Message.GET_CLIENTS, clientNames);
        serverConnection.broadcast(broadcastMessage);
    }
}
