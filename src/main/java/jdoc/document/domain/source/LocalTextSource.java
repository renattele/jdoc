package jdoc.document.domain.source;

import java.io.File;
import java.io.IOException;

public interface LocalTextSource extends TextSource {
    interface Factory {
        LocalTextSource create(File file, boolean emitChangesFromFile) throws IOException;
    }
}
