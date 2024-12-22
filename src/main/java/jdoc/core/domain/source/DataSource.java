package jdoc.core.domain.source;

import io.reactivex.rxjava3.core.Flowable;
import jdoc.core.domain.change.Change;

public interface DataSource<CHANGE extends Change<?, CHANGE>> extends AutoCloseable {
    void apply(CHANGE change);
    Flowable<CHANGE> changes();
    default boolean populatesChanges() {
        return true;
    };
}
