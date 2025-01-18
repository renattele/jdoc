package jdoc.core.net.client.impl;

import jdoc.core.net.client.ClientConnection;
import jdoc.core.net.client.ClientConnectionDataSource;
import jdoc.core.net.protocol.ProtocolConstants;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MapClientConnectionDataSource implements ClientConnectionDataSource {
    private final Map<String, ClientConnection> connections;

    public MapClientConnectionDataSource() {
        connections = new HashMap<>();
    }

    @Override
    public synchronized ClientConnection get(String url) throws IOException {
        if (connections.containsKey(url) && !connections.get(url).isConnected()) {
            connections.remove(url);
        }
        if (!connections.containsKey(url)) {
            connections.put(url, new SocketClientConnection(url, ProtocolConstants.PORT));
        }
        return connections.get(url);
    }
}
