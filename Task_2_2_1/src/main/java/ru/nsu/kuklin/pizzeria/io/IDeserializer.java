package ru.nsu.kuklin.pizzeria.io;

/**
 *  Interface for general deserialization of objects.
 */
public interface IDeserializer<T> {
    /**
     * Return all deserialized objects.
     */
    T[] read();
}
