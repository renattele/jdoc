package jdoc.core.net.server.impl;

import jdoc.core.net.protocol.ProtocolConstants;
import jdoc.core.net.server.ServerConnection;
import jdoc.core.net.server.ServerConnectionDataSource;

import java.io.IOException;

public class MapServerConnectionDataSource implements ServerConnectionDataSource {
    private ServerConnection serverConnection;
    @Override
    public synchronized ServerConnection get() throws IOException {
        if (serverConnection == null) {
            serverConnection = new SocketServerConnection(ProtocolConstants.PORT);
        }
        return serverConnection;
    }
}
