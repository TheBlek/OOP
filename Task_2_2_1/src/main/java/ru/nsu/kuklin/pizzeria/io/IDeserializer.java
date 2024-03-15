package ru.nsu.kuklin.pizzeria.io;

import java.io.File;

/**
 *  Interface for general deserialization of objects.
 */
public interface IDeserializer<T> {
    /**
     * Return all deserialized objects.
     */
    T[] read();
}
