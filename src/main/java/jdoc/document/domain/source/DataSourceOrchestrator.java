package jdoc.document.domain.source;

import jdoc.document.domain.change.Change;

public interface DataSourceOrchestrator<CHANGE extends Change<?, CHANGE>> extends AutoCloseable {
    void addSource(DataSource<CHANGE> source);
    void removeSource(DataSource<CHANGE> source);

    interface Factory {
        @SuppressWarnings("unchecked")
        <CHANGE extends Change<?, CHANGE>> DataSourceOrchestrator<CHANGE> create(DataSource<CHANGE>... dataSources);
    }
}
