package jdoc.core.data.source;

import jdoc.core.domain.change.Change;
import jdoc.core.domain.source.DataSource;
import jdoc.core.domain.source.DataSourceOrchestrator;
import jdoc.core.util.DisposableList;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class BlockingDataSourceOrchestrator<CHANGE extends Change<?, CHANGE>> implements DataSourceOrchestrator<CHANGE> {
    private final DisposableList disposables = new DisposableList();
    private final Set<DataSource<CHANGE>> sources = new HashSet<>();
    private CHANGE reducedChange;
    private final Object lock = new Object();

    @SafeVarargs
    public BlockingDataSourceOrchestrator(DataSource<CHANGE>... sources) {
        for (DataSource<CHANGE> source : sources) {
            addSource(source);
        }
    }

    private synchronized void apply(DataSource<CHANGE> thiz, CHANGE change) {
        if (reducedChange == null) {
            reducedChange = change;
        } else {
            reducedChange = change.reduce(List.of(reducedChange, change));
        }
        for (DataSource<CHANGE> source : sources) {
            if (source == thiz) continue;
            System.out.println("APPLYING FOR: " + source);
            source.apply(change);
        }
    }

    @Override
    public synchronized void addSource(DataSource<CHANGE> source) {
        var blockingSource = new BlockingDataSource<>(source);
        disposables.add(blockingSource.changes().subscribe(change -> {
            synchronized (lock) {
                System.out.println("BROADCASTING CHANGE: " + change + " FROM SOURCE: " + source);
                apply(blockingSource, change);
            }
        }));
        if (reducedChange != null) {
            blockingSource.apply(reducedChange);
        }
        sources.add(blockingSource);
    }

    @Override
    public void removeSource(DataSource<CHANGE> source) {
        sources.removeIf(s -> s.equals(source));
    }

    @Override
    public void close() throws Exception {
        disposables.close();
    }

    public static class Factory implements DataSourceOrchestrator.Factory {

        @SafeVarargs
        @Override
        public final <T extends Change<?, T>> DataSourceOrchestrator<T> create(DataSource<T>... sources) {
            return new BlockingDataSourceOrchestrator<>(sources);
        }
    }
}