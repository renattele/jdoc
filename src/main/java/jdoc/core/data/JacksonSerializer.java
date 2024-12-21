package jdoc.core.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdoc.core.domain.Serializer;

public class JacksonSerializer implements Serializer {
    private final ObjectMapper mapper;

    public JacksonSerializer() {
        mapper = new ObjectMapper();
    }

    @Override
    public String toString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <T> T fromString(String str, Class<T> clazz) {
        try {
            return mapper.readValue(str, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
