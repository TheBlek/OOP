package ru.nsu.kuklin.pizzeria.worker;

import ru.nsu.kuklin.pizzeria.State;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract factory that produces T workers from their data D.
 */
public abstract class WorkerFactory<T extends Worker, D> {
    /**
     * Initialize factory with pizzeria state.
     */
    public WorkerFactory(State state) {
        this.state = state;
    }

    /**
     * Main method.
     */
    public abstract T construct(D data);

    /**
     * Provided method of mapping an array into a list.
     */
    public List<T> map(D[] data) {
        return Arrays.stream(data).map(this::construct).toList();
    }

    protected final State state;
}
