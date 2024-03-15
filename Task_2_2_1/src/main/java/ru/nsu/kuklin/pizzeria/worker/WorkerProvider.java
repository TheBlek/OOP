package ru.nsu.kuklin.pizzeria.worker;

import ru.nsu.kuklin.pizzeria.io.IDeserializer;

import java.util.Arrays;
import java.util.List;

/**
 * Provider of workers based on a data deserializer and a factory.
 */
public class WorkerProvider<T extends Worker, D> {
    /**
     * Construct provider from given deserializer and factory.
     */
    public WorkerProvider(IDeserializer<D> deserializer, WorkerFactory<T, D> factory) {
        this.deserializer = deserializer;
        this.factory = factory;
    }

    public List<T> get() {
        return factory.map(deserializer.read());
    }
    private final IDeserializer<D> deserializer;
    private final WorkerFactory<T, D> factory;
}
