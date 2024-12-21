package jdoc.document.domain.source.text;

import java.io.IOException;

public interface RemoteTextSource extends TextSource {
    interface Factory {
        RemoteTextSource create(String url) throws IOException;
    }
}
