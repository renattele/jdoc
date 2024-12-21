package jdoc.core.net.client;

import java.io.IOException;

public interface ClientConnectionDataSource {
    ClientConnection get(String url) throws IOException;
}
