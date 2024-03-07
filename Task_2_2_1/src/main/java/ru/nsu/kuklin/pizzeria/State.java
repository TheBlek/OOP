package ru.nsu.kuklin.pizzeria;

import ru.nsu.kuklin.pizzeria.queue.BlockingDeque;

public class State {
    public State(int storageSize) {
        orders = new BlockingDeque<Order>(100);
        storage = new BlockingDeque<Order>(storageSize);
    }

    private final BlockingDeque<Order> orders;
    private final BlockingDeque<Order> storage;

    public BlockingDeque<Order> getOrders() {
        return orders;
    }

    public BlockingDeque<Order> getStorage() {
        return storage;
    }
}
