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
        try {
            for (var order : toPlace) {
                orders.put(order);
                logger.log("Placed order " + order.name());
            }
        } catch (Exception ignored) {}
        logger.log("Going home");
    }

    private final BlockingDeque<Order> orders;
    private final Order[] toPlace;
}
