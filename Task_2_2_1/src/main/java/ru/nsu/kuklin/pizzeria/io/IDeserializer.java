package ru.nsu.kuklin.pizzeria.io;

import java.io.File;

public interface IDeserializer<T> {
    T[] read();
}
