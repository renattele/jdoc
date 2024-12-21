package jdoc.document.domain.source.text;

import java.io.File;
import java.io.IOException;

public interface LocalTextSource extends TextSource {
    interface Factory {
        LocalTextSource create(File file) throws IOException;
    }
}
