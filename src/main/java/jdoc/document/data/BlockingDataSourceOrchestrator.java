package jdoc.document.data;

import io.reactivex.rxjava3.disposables.Disposable;
import jdoc.document.data.source.BlockingDataSource;
import jdoc.document.domain.change.Change;
import jdoc.document.domain.source.DataSource;
import jdoc.document.domain.source.DataSourceOrchestrator;

import java.util.*;

public class BlockingDataSourceOrchestrator<CHANGE extends Change<?, CHANGE>> implements DataSourceOrchestrator<CHANGE> {
    private final List<Disposable> disposables = new ArrayList<>();
    private final Set<DataSource<CHANGE>> textSources = new HashSet<>();
    private final Object lock = new Object();

    @SafeVarargs
    public BlockingDataSourceOrchestrator(DataSource<CHANGE>... sources) {
        for (DataSource<CHANGE> source : sources) {
            addSource(source);
        }
    }

    private synchronized void apply(DataSource<CHANGE> thiz, CHANGE textChange) {
        for (DataSource<CHANGE> textSource : textSources) {
            if (textSource == thiz) continue;
            System.out.println("APPLYING FOR: " + textSource);
            textSource.apply(textChange);
        }
    }

    @Override
    public synchronized void addSource(DataSource<CHANGE> source) {
        var blockingSource = new BlockingDataSource<>(source);
        disposables.add(blockingSource.changes().subscribe(change -> {
            synchronized (lock) {
                apply(blockingSource, change);
            }
        }));
        textSources.add(blockingSource);
    }

    @Override
    public void removeSource(DataSource<CHANGE> source) {
        textSources.removeIf(s -> s.equals(source));
    }

    @Override
    public void close() {
        for (var disposable : disposables) {
            disposable.dispose();
        }
    }

    public static class Factory implements DataSourceOrchestrator.Factory {

        @SafeVarargs
        @Override
        public final <T extends Change<?, T>> DataSourceOrchestrator<T> create(DataSource<T>... sources) {
            return new BlockingDataSourceOrchestrator<>(sources);
        }
    }
}