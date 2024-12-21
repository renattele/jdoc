package jdoc.document.data.source;

import io.reactivex.rxjava3.core.Flowable;
import jdoc.document.domain.change.Change;
import jdoc.document.domain.source.DataSource;

import java.util.Optional;

public class BlockingDataSource<CHANGE extends Change<?, CHANGE>> implements DataSource<CHANGE> {
    private final DataSource<CHANGE> origin;
    private final Flowable<CHANGE> changes;
    private volatile boolean shouldSkip = false;

    public BlockingDataSource(DataSource<CHANGE> origin) {
        this.origin = origin;
        this.changes = origin.changes().mapOptional(change -> {
            synchronized (origin) {
                System.out.println("INTERNAL CHANGE: " + change + ". SOURCE: " + origin + ". SHOULD SKIP: " + shouldSkip);
                if (shouldSkip && origin.populatesChanges()) {
                    shouldSkip = false;
                    return Optional.empty();
                }
                return Optional.of(change);
            }
        }).cache();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockingDataSource) return super.equals(obj);
        return origin.equals(obj);
    }

    @Override
    public String toString() {
        return origin.toString();
    }

    @Override
    public void close() throws Exception {
        origin.close();
    }

    @Override
    public synchronized void apply(CHANGE textChange) {
        System.out.println("EXTERNAL CHANGE: " + textChange + ". APPLYING TO: " + origin + ". SHOULD SKIP: " + shouldSkip);
        origin.apply(textChange);
        shouldSkip = true;
    }

    @Override
    public Flowable<CHANGE> changes() {
        return changes;
    }
}
