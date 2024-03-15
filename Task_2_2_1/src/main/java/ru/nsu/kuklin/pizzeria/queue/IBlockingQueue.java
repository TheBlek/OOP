package ru.nsu.kuklin.pizzeria.queue;

/**
 * Blocking queue with a fixed capacity that allows different threads wait on the result of others.
 */
public interface IBlockingQueue<T> {
    /**
     * Puts an element into the queue blocking until there is enough space.
     */
    void put(T e) throws InterruptedException;

    /**
     * Gets first element from the queue blocking until there is one.
     */
    T get() throws InterruptedException;

    /**
     * Returns current size (not capacity) of the queue.
     */
    int getSize();
}
