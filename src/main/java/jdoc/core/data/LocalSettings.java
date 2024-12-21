package jdoc.core.data;

import jdoc.core.domain.Settings;
import jdoc.core.domain.Serializer;

import java.util.prefs.Preferences;

public class LocalSettings implements Settings {
    private final Preferences preferences = Preferences.userNodeForPackage(LocalSettings.class);
    private final Serializer serializer;

    public LocalSettings(Serializer serializer) {
        this.serializer = serializer;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, T defaultValue) {
        var value = preferences.get(key, null);
        if (value == null) return defaultValue;
        return (T) serializer.fromString(value, defaultValue.getClass());
    }

    @Override
    public <T> void put(String key, T value) {
        preferences.put(key, serializer.toString(value));
    }

    @Override
    public void remove(String key) {
        preferences.remove(key);
    }
}
