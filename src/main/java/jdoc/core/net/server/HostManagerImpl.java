package jdoc.core.net.server;

import jdoc.core.domain.Serializer;

import java.util.HashMap;
import java.util.Map;

public class HostManagerImpl {
    private final ServerConnection serverConnection;
    private final Serializer serializer;
    private final Map<String, String> clients = new HashMap<>();

    public HostManagerImpl(ServerConnection serverConnection, Serializer serializer) {
        this.serverConnection = serverConnection;
        this.serializer = serializer;
    }
}
