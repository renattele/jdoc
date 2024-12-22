package jdoc.document.domain;

import jdoc.document.domain.source.Document;
import jdoc.document.domain.source.TextSource;

public interface DocumentRepository {
    Document getRemoteDocument(TextSource textSource, String url);
    Document getLocalDocument(TextSource textSource, String url);
}
