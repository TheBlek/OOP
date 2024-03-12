package ru.nsu.kuklin.pizzeria.deliverer;

import ru.nsu.kuklin.pizzeria.Order;
import ru.nsu.kuklin.pizzeria.io.ILogger;
import ru.nsu.kuklin.pizzeria.queue.BlockingDeque;
import ru.nsu.kuklin.pizzeria.worker.Worker;

public class Deliverer extends Worker {
    public Deliverer(ILogger logger, BlockingDeque<Order> storage, float deliveryTime) {
        super(logger);
        this.storage = storage;
        this.deliveryTime = deliveryTime;
    }

    @Override
    public void run() {
        try {
            while (true) {
                logger.log("Ready to deliver order");
                var order = storage.get();
                logger.log("Delivering order " + order);
                Thread.sleep((int)(deliveryTime * 1000));
            }
        } catch(InterruptedException ignored) {}
        logger.log("Going home");
    }
    private final float deliveryTime;
    private final BlockingDeque<Order> storage;
}
