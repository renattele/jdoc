package jdoc.core.data;

import jdoc.core.di.Module;

import java.util.HashMap;
import java.util.Map;

public class ReadOnlyModule implements Module {
    private final Map<Class<?>, Object> map;

    public ReadOnlyModule(Object... objects) {
        map = new HashMap<>(objects.length);
        for (Object object : objects) {
            Class<?> classRepresentation = object.getClass();
            var interfaces = object.getClass().getInterfaces();
            if (interfaces.length > 0) classRepresentation = interfaces[0];
            map.put(classRepresentation, object);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> clazz) {
        return  (T) map.get(clazz);
    }
}
