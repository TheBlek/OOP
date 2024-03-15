package ru.nsu.kuklin.pizzeria.customer;

import ru.nsu.kuklin.pizzeria.Order;
import ru.nsu.kuklin.pizzeria.io.ILogger;
import ru.nsu.kuklin.pizzeria.queue.BlockingDeque;
import ru.nsu.kuklin.pizzeria.worker.Worker;

import java.util.List;

public class Customer extends Worker {
    public Customer(ILogger logger, BlockingDeque<Order> orders, Order[] toPlace) {
        super(logger);
        this.orders = orders;
        this.toPlace = toPlace;
    }

    @Override
    public void run() {
        int i = 0;

        try {
            while (i < toPlace.length && !shouldStop) {
                orders.put(toPlace[i]);
                logger.log("Placed order " + toPlace[i].name());
                i++;
            }
        } catch (InterruptedException ignored) {}
        logger.log("Going home");
    }

    public BlockingDeque<Order> getOrders() {
        return orders;
    }

    public Order[] getToPlace() {
        return toPlace;
    }

    private final BlockingDeque<Order> orders;
    private final Order[] toPlace;
}
