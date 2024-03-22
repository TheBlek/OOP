package ru.nsu.kuklin.pizzeria.customer;

import ru.nsu.kuklin.pizzeria.Order;
import ru.nsu.kuklin.pizzeria.io.ILogger;
import ru.nsu.kuklin.pizzeria.queue.BlockingQueue;
import ru.nsu.kuklin.pizzeria.queue.IBlockingQueue;
import ru.nsu.kuklin.pizzeria.worker.Worker;

/**
 * Worker that places new orders in the queue.
 */
public class Customer extends Worker {
    /**
     * Construct deliverer.
     */
    public Customer(ILogger logger, BlockingQueue<Order> orders, Order[] toPlace) {
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
        } catch (InterruptedException ignored) { }
        logger.log("Going home");
    }

    /**
     * Get attached orders queue.
     */
    public IBlockingQueue<Order> getOrders() {
        return orders;
    }

    /**
     * Get orders that customer will place during his lifetime.
     */
    public Order[] getToPlace() {
        return toPlace;
    }

    private final IBlockingQueue<Order> orders;
    private final Order[] toPlace;
}
