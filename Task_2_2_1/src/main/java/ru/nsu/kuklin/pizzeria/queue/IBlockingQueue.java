package ru.nsu.kuklin.pizzeria.queue;

public interface IBlockingQueue<T> {
    void put(T e) throws InterruptedException;
    T get() throws InterruptedException;
}
