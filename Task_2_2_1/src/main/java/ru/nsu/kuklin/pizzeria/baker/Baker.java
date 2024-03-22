package ru.nsu.kuklin.pizzeria.baker;

import ru.nsu.kuklin.pizzeria.Order;
import ru.nsu.kuklin.pizzeria.worker.Worker;
import ru.nsu.kuklin.pizzeria.io.ILogger;
import ru.nsu.kuklin.pizzeria.queue.IBlockingQueue;

/**
 * Worker that takes orders from order queue and places them in storage on completion.
 */
public class Baker extends Worker {
    /**
     * Construct baker.
     */
    public Baker(
        ILogger logger,
        IBlockingQueue<Order> orders,
        IBlockingQueue<Order> storage,
        float timeToPizza) {
        super(logger);
        this.orders = orders;
        this.storage = storage;
        this.timeToPizza = timeToPizza;
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        try {
            while (!shouldStop) {
                logger.log("Ready to take order");
                Order order = orders.get();
                logger.log("Took order " + order.name());
                Thread.sleep((int) (timeToPizza * 1000.f));
                logger.log("Finished order " + order.name());
                storage.put(order);
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
     * Get attached storage queue.
     */
    public IBlockingQueue<Order> getStorage() {
        return storage;
    }

    /**
     * Get time spent on one pizza.
     */
    public float getTimeToPizza() {
        return timeToPizza;
    }

    private final IBlockingQueue<Order> orders;
    private final IBlockingQueue<Order> storage;
    private final float timeToPizza;
}
