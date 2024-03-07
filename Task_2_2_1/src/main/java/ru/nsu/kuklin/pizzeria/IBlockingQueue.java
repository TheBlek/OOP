package ru.nsu.kuklin.pizzeria;

public interface IBlockingQueue<T> {
    void put(T e) throws InterruptedException;
    T get() throws InterruptedException;
}
