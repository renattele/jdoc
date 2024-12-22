package jdoc.core.domain.change;

import java.util.List;

public interface Change<SRC, T extends Change<SRC, T>> {
    void apply(SRC to);
    T reduce(T change);

    @SuppressWarnings("unchecked")
    default T reduce(List<T> changes) {
        if (changes.isEmpty()) return (T) this;
        var reduced = reduce(changes.get(0));
        for (T change : changes) {
            reduced = reduced.reduce(change);
        }
        return reduced;
    }
}
