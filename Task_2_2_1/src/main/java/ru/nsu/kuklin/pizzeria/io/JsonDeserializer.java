package ru.nsu.kuklin.pizzeria.io;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Deserializer implementation from a json format file.
 */
@SuppressWarnings("unchecked")
public class JsonDeserializer<T> implements IDeserializer<T> {
    /**
     *  Construct a deserializer for given type from a given file.
     */
    public JsonDeserializer(Class<T> type, File file) {
        this.type = type;
        this.file = file;
        this.mapper = new ObjectMapper();
    }

    @Override
    public T[] read() {
        try {
            return (T[]) mapper.readValue(file, type.arrayType());
        } catch (IOException e) {
            System.out.printf("Failed to deserialize file %s: %s", file, e);
        }
        return (T[]) new Object[0];
    }

    private final Class<T> type;
    private final ObjectMapper mapper;
    private final File file;
}
