package ru.nsu.kuklin.pizzeria.worker;

import ru.nsu.kuklin.pizzeria.io.ILogger;

/**
 * Abstract worker class representing one task running in pizzeria.
 */
public abstract class Worker implements Runnable {
    /**
     * Constructs from the logger.
     */
    public Worker(ILogger logger) {
        this.logger = logger;
    }

    /**
     * Indicate that this worker should stop working.
     */
    public void stop() {
        shouldStop = true;
    }

    protected final ILogger logger;
    protected volatile Boolean shouldStop = false;
}
