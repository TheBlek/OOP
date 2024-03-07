package ru.nsu.kuklin.pizzeria.io;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("unchecked")
public class JsonDeserializer<T> implements IDeserializer<T> {
    public JsonDeserializer(Class<T> type, File file) {
        this.type = type;
        this.file = file;
        this.mapper = new ObjectMapper();
    }

    @Override
    public T[] read() {
        try {
            return (T[]) mapper.readValue(file, type.arrayType());
        } catch (IOException ignored) {}
        return (T[]) new Object[0];
    }

    private final Class<T> type;
    private final ObjectMapper mapper;
    private final File file;
}
