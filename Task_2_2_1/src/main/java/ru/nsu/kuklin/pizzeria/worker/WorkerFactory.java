package ru.nsu.kuklin.pizzeria.worker;

import ru.nsu.kuklin.pizzeria.State;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public abstract class WorkerFactory<T extends Worker, D> {
    public WorkerFactory(State state) {
        this.state = state;
    }

    public abstract T construct(D data);
    public List<T> map(D[] data) {
        return Arrays.stream(data).map(this::construct).collect(Collectors.toList());
    }

    protected final State state;
    protected Class<?> type = null;
}
