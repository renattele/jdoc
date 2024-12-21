package jdoc.document.domain;

import jdoc.document.domain.source.text.Document;
import jdoc.document.domain.source.text.TextSource;

public interface DocumentRepository {
    Document getRemoteDocument(TextSource textSource, String url);
    Document getLocalDocument(TextSource textSource, String url);
}
