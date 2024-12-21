package jdoc.core.di;

public interface Module {
    <T> T get(Class<T> clazz);
}
