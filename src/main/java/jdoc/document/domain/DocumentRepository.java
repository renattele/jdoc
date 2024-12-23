package jdoc.document.domain;

import jdoc.document.domain.source.Document;
import jdoc.document.domain.source.TextSource;

public interface DocumentRepository {
    Document getRemoteDocument(String url);
    Document getLocalDocument(String url);
}
