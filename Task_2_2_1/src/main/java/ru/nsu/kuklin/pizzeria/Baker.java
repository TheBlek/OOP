package ru.nsu.kuklin.pizzeria;

import java.util.function.Consumer;

public class Baker extends Worker {
    public Baker(ILogger logger, IBlockingQueue<Order> orders, IBlockingQueue<Order> storage) {
        super(logger);
        this.orders = orders;
        this.storage = storage;
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        while (true) {
            try {
                logger.log("Ready to take order");
                Order order = orders.get();
                logger.log("Took order " + order.name());
                Thread.sleep(1000);
                logger.log("Finished order " + order.name());
                storage.put(order);
            } catch (Exception e) {
                logger.log("Going home");
                return;
            }
        }
    }

    private final IBlockingQueue<Order> orders;
    private final IBlockingQueue<Order> storage;
}
