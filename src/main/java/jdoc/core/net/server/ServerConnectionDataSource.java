package jdoc.core.net.server;

import java.io.IOException;

public interface ServerConnectionDataSource {
    ServerConnection get() throws IOException;
}
