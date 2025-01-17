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
        if (!connections.containsKey(url)) {
            var uri = URI.create(url);
            var port = uri.getPort() < 0 ? ProtocolConstants.PORT : uri.getPort();
            connections.put(url, new SocketClientConnection(uri.getHost(), port));
        }
        return connections.get(url);
    }
}
