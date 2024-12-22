package jdoc.core.util;

import io.reactivex.rxjava3.disposables.Disposable;

import java.io.Closeable;
import java.util.LinkedList;
import java.util.List;

public class DisposableList implements AutoCloseable {
    private final List<Disposable> disposables = new LinkedList<>();
    private final List<Closeable> closeables = new LinkedList<>();
    private final List<AutoCloseable> autoCloseables = new LinkedList<>();

    public Disposable add(Disposable disposable) {
        disposables.add(disposable);
        return disposable;
    }
    public Closeable add(Closeable closeable) {
        closeables.add(closeable);
        return closeable;
    }
    public AutoCloseable add(AutoCloseable closeable) {
        autoCloseables.add(closeable);
        return closeable;
    }
    @Override
    public void close() throws Exception {
        disposables.forEach(Disposable::dispose);
        disposables.clear();
        for (Closeable closeable : closeables) {
            closeable.close();
        }
        closeables.clear();
        for (AutoCloseable autoCloseable : autoCloseables) {
            autoCloseable.close();
        }
        autoCloseables.clear();
    }
}
