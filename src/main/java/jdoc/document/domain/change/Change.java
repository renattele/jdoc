package jdoc.document.domain.change;

public interface Change<SRC, T extends Change<SRC, T>> {
    void apply(SRC to);
    T reduce(Iterable<T> changes);
}
