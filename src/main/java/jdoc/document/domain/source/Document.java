package jdoc.document.domain.source;

import jdoc.document.domain.change.TextChange;
import jdoc.core.domain.source.DataSource;
import jdoc.core.domain.source.DataSourceOrchestrator;

public interface Document extends DataSourceOrchestrator<TextChange> {
    static Document wrap(DataSourceOrchestrator<TextChange> orchestrator) {
        return new Document() {
            @Override
            public void addSource(DataSource<TextChange> source) {
                orchestrator.addSource(source);
            }

            @Override
            public void removeSource(DataSource<TextChange> source) {
                orchestrator.removeSource(source);
            }

            @Override
            public void close() throws Exception {
                orchestrator.close();
            }
        };
    }
}
