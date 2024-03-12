package ru.nsu.kuklin.pizzeria.baker;

import ru.nsu.kuklin.pizzeria.State;
import ru.nsu.kuklin.pizzeria.io.ILogger;
import ru.nsu.kuklin.pizzeria.io.WorkerLogger;
import ru.nsu.kuklin.pizzeria.worker.Worker;
import ru.nsu.kuklin.pizzeria.worker.WorkerFactory;

public class DefaultBakerFactory extends WorkerFactory<Baker, BakerData> {
    public DefaultBakerFactory(State state) {
        super(state);
        type = Baker.class;
    }

    @Override
    public Baker construct(BakerData data) {
        return new Baker(new WorkerLogger(Baker.class, count++), state.getOrders(), state.getStorage(), data.timePerPizza());
    }
    private int count = 0;
}
