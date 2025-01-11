package jdoc.document.domain;

import jdoc.document.domain.source.Document;

public interface DocumentRepository {
    Document getRemoteDocument(String url);
}
