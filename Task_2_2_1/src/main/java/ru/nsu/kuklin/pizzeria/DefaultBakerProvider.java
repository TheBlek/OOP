package ru.nsu.kuklin.pizzeria;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class DefaultBakerProvider extends WorkerProvider {
    public DefaultBakerProvider(State state, int cnt) {
        super(state);
        this.count = cnt;
    }

    @NotNull
    @Override
    public Iterator<Worker> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return id < count;
            }

            @Override
            public Worker next() {
                return new Baker(new WorkerLogger(Baker.class, id++), state.getOrders(), state.getStorage());
            }
            private int id;
        };
    }
    private final int count;
}
