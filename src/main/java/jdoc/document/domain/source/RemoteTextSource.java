package jdoc.document.domain.source;

import java.io.IOException;

public interface RemoteTextSource extends TextSource {
    interface Factory {
        RemoteTextSource create(String url) throws IOException;
    }
}
