package ru.nsu.kuklin.pizzeria.deliverer;

import ru.nsu.kuklin.pizzeria.Order;
import ru.nsu.kuklin.pizzeria.io.ILogger;
import ru.nsu.kuklin.pizzeria.queue.IBlockingQueue;
import ru.nsu.kuklin.pizzeria.worker.Worker;

/**
 * Worker that delivers orders from storage
 */
public class Deliverer extends Worker {
    /**
     * Construct deliverer.
     */
    public Deliverer(ILogger logger, IBlockingQueue<Order> storage, float deliveryTime) {
        super(logger);
        this.storage = storage;
        this.deliveryTime = deliveryTime;
    }

    @Override
    public void run() {
        try {
            while (!shouldStop) {
                logger.log("Ready to deliver order");
                var order = storage.get();
                logger.log("Delivering order " + order);
                Thread.sleep((int)(deliveryTime * 1000.f));
            }
        } catch(InterruptedException ignored) {}
        logger.log("Going home");
    }

    /**
     * Get for one order delivery.
     */
    public float getDeliveryTime() {
        return deliveryTime;
    }

    /**
     * Get attached storage.
     */
    public IBlockingQueue<Order> getStorage() {
        return storage;
    }

    private final float deliveryTime;

    private final IBlockingQueue<Order> storage;
}
