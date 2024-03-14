package ru.nsu.kuklin.pizzeria;

import org.junit.jupiter.api.Test;
import ru.nsu.kuklin.pizzeria.baker.BakerData;
import ru.nsu.kuklin.pizzeria.baker.DefaultBakerFactory;
import ru.nsu.kuklin.pizzeria.customer.CustomerData;
import ru.nsu.kuklin.pizzeria.customer.DefaultCustomerFactory;
import ru.nsu.kuklin.pizzeria.deliverer.DefaultDelivererFactory;
import ru.nsu.kuklin.pizzeria.deliverer.DelivererData;
import ru.nsu.kuklin.pizzeria.queue.BlockingDeque;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

public class WorkerTest {
    @Test
    public void testBaker() {
        var state = new State(100);
        try {
            state.getOrders().put(new Order(0, "PIZZA"));
        } catch (Exception e) {
            fail();
        }
        BakerData data = new BakerData("John", 1.5f);
        var factory = new DefaultBakerFactory(state);
        var thread = new Thread(factory.construct(data));

        var start = System.nanoTime();
        thread.start();
        Order out = null;
        try {
            out = state.getStorage().get();
        } catch (Exception e) {
            fail();
        }
        var end = System.nanoTime();
        assertEquals(data.timePerPizza() * 1000.f, TimeUnit.NANOSECONDS.toMillis(end - start), 50.f);
        assertEquals(new Order(0, "PIZZA"), out);
    }
    @Test
    public void testDeliverer() {
        var state = new State(1);
        try {
            state.getStorage().put(new Order(0, "PIZZA"));
        } catch (Exception e) {
            fail();
        }
        var data = new DelivererData("John", 1.5f);
        var factory = new DefaultDelivererFactory(state);
        var thread = new Thread(factory.construct(data));

        var start = System.nanoTime();
        thread.start();
        try {
            // This will execute instantly (empty space in storage)
            state.getStorage().put(new Order(1, "Stub"));
            // This will execute only after the prev order is taken - hence the original one is delivered
            state.getStorage().put(new Order(1, "Stub"));
        } catch (Exception e) {
            fail();
        }
        var end = System.nanoTime();
        assertEquals(data.deliveryTime() * 1000.f, TimeUnit.NANOSECONDS.toMillis(end - start), 50.f);
    }
    @Test
    public void testConsumers() {
        var state = new State(10);
        var data = new CustomerData("John", new Order[] {
                new Order(0, "PIZZA"),
                new Order(0, "PIZZA123"),
                new Order(0, "P"),
        });
        var factory = new DefaultCustomerFactory(state);
        var thread = new Thread(factory.construct(data));

        thread.start();
        try {
            for (var order : data.orders())
                assertEquals(order, state.getOrders().get());
        } catch (Exception e) {
            fail();
        }
    }
}
