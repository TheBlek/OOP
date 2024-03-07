package ru.nsu.kuklin.pizzeria;

public abstract class WorkerProvider implements Iterable<Worker> {
    public WorkerProvider(State state) {
        this.state = state;
    }

    protected final State state;
}
