package jdoc.core.data.source;

import io.reactivex.rxjava3.core.Flowable;
import jdoc.core.domain.change.Change;
import jdoc.core.domain.source.DataSource;

import java.util.Optional;

public class BlockingDataSource<CHANGE extends Change<?, CHANGE>> implements DataSource<CHANGE> {
    private final DataSource<CHANGE> origin;
    private final Flowable<CHANGE> changes;
    private volatile CHANGE lastChange;

    public BlockingDataSource(DataSource<CHANGE> origin) {
        this.origin = origin;
        this.changes = origin.changes().mapOptional(change -> {
            synchronized (origin) {
                System.out.println("INTERNAL CHANGE: " + change + ". SOURCE: " + origin + ". LAST CHANGE: " + lastChange);
                if (change.equals(lastChange) && origin.populatesChanges()) {
                    lastChange = null;
                    return Optional.empty();
                }
                return Optional.of(change);
            }
        }).map(change -> {
            System.out.println("ACTUAL CHANGE: " + change + ". SOURCE: " + origin + ". LAST CHANGE: " + lastChange);
            return change;
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
    public synchronized void apply(CHANGE change) {
        System.out.println("EXTERNAL CHANGE: " + change + ". APPLYING TO: " + origin + ". LAST CHANGE: " + lastChange);
        lastChange = change;
        origin.apply(change);
    }

    @Override
    public Flowable<CHANGE> changes() {
        return changes;
    }
}
