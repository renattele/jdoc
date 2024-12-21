package jdoc.core.domain;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Serializer {
    String toString(Object obj);

    <T> T fromString(String str, Class<T> clazz);

    static Serializer from(Function<Object, String> toString, BiFunction<String, Class<?>, Object> fromString) {
        return new Serializer() {
            @Override
            public String toString(Object obj) {
                return toString.apply(obj);
            }

            @Override
            public <T> T fromString(String str, Class<T> clazz) {
                return (T) fromString.apply(str, clazz);
            }
        };
    }
}
