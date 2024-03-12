package ru.nsu.kuklin.pizzeria.customer;

import ru.nsu.kuklin.pizzeria.State;
import ru.nsu.kuklin.pizzeria.io.WorkerLogger;
import ru.nsu.kuklin.pizzeria.worker.WorkerFactory;

public class DefaultCustomerFactory extends WorkerFactory<Customer, CustomerData> {
    public DefaultCustomerFactory(State state) {
        super(state);
    }

    @Override
    public Customer construct(CustomerData data) {
        return new Customer(new WorkerLogger(Customer.class, data.name()), state.getOrders(), data.orders());
    }
}
