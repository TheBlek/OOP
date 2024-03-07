package ru.nsu.kuklin.pizzeria;

public abstract class Worker implements Runnable {
    public Worker(ILogger logger) {
        this.logger = logger;
    }
    protected final ILogger logger;
}
