package jdoc.core.domain;

public interface Settings {
    <T> T get(String key, T defaultValue);
    <T> void put(String key, T value);
    void remove(String key);
}
