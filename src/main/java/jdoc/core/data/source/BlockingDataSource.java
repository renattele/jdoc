package jdoc.core.data.source;

import io.reactivex.rxjava3.core.Flowable;
import jdoc.core.domain.change.Change;
import jdoc.core.domain.source.DataSource;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class BlockingDataSource<CHANGE extends Change<?, CHANGE>> implements DataSource<CHANGE> {
    private final DataSource<CHANGE> origin;
    private final Flowable<CHANGE> changes;
    private volatile CHANGE lastChange;

    public BlockingDataSource(DataSource<CHANGE> origin) {
        this.origin = origin;
        this.changes = origin.changes().mapOptional(change -> {
            synchronized (origin) {
                log.info("INTERNAL CHANGE: {}. SOURCE: {}. LAST CHANGE: {}", change, origin, lastChange);
                if (change.equals(lastChange) && origin.populatesChanges()) {
                    lastChange = null;
                    return Optional.empty();
                }
                return Optional.of(change);
            }
        }).map(change -> {
            log.info("ACTUAL CHANGE: {}. SOURCE: {}. LAST CHANGE: {}", change, origin, lastChange);
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
        log.info("EXTERNAL CHANGE: {}. APPLYING TO: {}. LAST CHANGE: {}", change, origin, lastChange);
        lastChange = change;
        origin.apply(change);
    }

    @Override
    public Flowable<CHANGE> changes() {
        return changes;
    }
}
