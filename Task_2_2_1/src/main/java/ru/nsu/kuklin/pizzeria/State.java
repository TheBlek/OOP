package ru.nsu.kuklin.pizzeria;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

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
