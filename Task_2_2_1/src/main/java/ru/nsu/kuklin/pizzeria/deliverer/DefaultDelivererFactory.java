package ru.nsu.kuklin.pizzeria.deliverer;

import ru.nsu.kuklin.pizzeria.State;
import ru.nsu.kuklin.pizzeria.io.WorkerLogger;
import ru.nsu.kuklin.pizzeria.worker.WorkerFactory;

public class DefaultDelivererFactory extends WorkerFactory<Deliverer, DelivererData> {
    public DefaultDelivererFactory(State state) {
        super(state);
    }

    @Override
    public Deliverer construct(DelivererData data) {
        return new Deliverer(new WorkerLogger(Deliverer.class, data.name()), state.getStorage(), data.deliveryTime());
    }
}
