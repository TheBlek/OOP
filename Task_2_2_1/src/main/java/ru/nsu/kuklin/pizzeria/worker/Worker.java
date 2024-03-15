package ru.nsu.kuklin.pizzeria.worker;

import ru.nsu.kuklin.pizzeria.io.ILogger;

public abstract class Worker implements Runnable {
    public Worker(ILogger logger) {
        this.logger = logger;
    }

    public void stop() {
        shouldStop = true;
    }
    protected final ILogger logger;
    protected volatile Boolean shouldStop = false;
}
