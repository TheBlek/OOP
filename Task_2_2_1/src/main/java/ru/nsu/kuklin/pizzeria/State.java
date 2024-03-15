package ru.nsu.kuklin.pizzeria;

import ru.nsu.kuklin.pizzeria.queue.BlockingQueue;

/**
 * Pizzeria state representation.
 */
public class State {
    public State(int storageSize, int queueSize) {
        orders = new BlockingQueue<Order>(queueSize);
        storage = new BlockingQueue<Order>(storageSize);
    }

    private final BlockingQueue<Order> orders;
    private final BlockingQueue<Order> storage;

    /**
     * Get blocking incoming order queue.
     */
    public BlockingQueue<Order> getOrders() {
        return orders;
    }

    /**
     * Get blocking storage queue.
     */
    public BlockingQueue<Order> getStorage() {
        return storage;
    }
}
