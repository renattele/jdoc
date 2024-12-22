package jdoc.document.data;

import jdoc.document.domain.*;
import jdoc.document.domain.change.TextChange;
import jdoc.core.domain.source.DataSourceOrchestrator;
import jdoc.core.domain.source.RemoteDataSource;
import jdoc.document.domain.source.Document;
import jdoc.document.domain.source.LocalTextSource;
import jdoc.document.domain.source.TextSource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("unchecked")
@AllArgsConstructor
@Slf4j
public class DocumentRepositoryImpl implements DocumentRepository {
    private final DataSourceOrchestrator.Factory orchestratorFactory;
    private final LocalTextSource.Factory localTextSourceFactory;
    private final RemoteDataSource.Factory<TextChange> remoteTextSourceFactory;

    @Override
    public Document getRemoteDocument(TextSource textSource, String url) {
        try {
            return Document.wrap(orchestratorFactory.create(
                    textSource,
                    remoteTextSourceFactory.create(url)
            ));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Document getLocalDocument(TextSource textSource, String url) {
        try {
            return Document.wrap(orchestratorFactory.create(
                    textSource,
                    localTextSourceFactory.create(new File(url)),
                    remoteTextSourceFactory.create("http://localhost")
            ));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
