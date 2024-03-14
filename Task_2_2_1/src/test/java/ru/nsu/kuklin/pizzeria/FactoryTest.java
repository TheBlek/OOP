package ru.nsu.kuklin.pizzeria;

import org.junit.jupiter.api.Test;
import ru.nsu.kuklin.pizzeria.baker.BakerData;
import ru.nsu.kuklin.pizzeria.baker.DefaultBakerFactory;
import ru.nsu.kuklin.pizzeria.customer.CustomerData;
import ru.nsu.kuklin.pizzeria.customer.DefaultCustomerFactory;
import ru.nsu.kuklin.pizzeria.deliverer.DefaultDelivererFactory;
import ru.nsu.kuklin.pizzeria.deliverer.DelivererData;
import ru.nsu.kuklin.pizzeria.queue.BlockingDeque;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FactoryTest {
    @Test
    public void testBakerFactory() {
        var state = new State(100);
        BakerData data = new BakerData("John", 82);
        var factory = new DefaultBakerFactory(state);
        var res = factory.construct(data);
        assertEquals(82, res.getTimeToPizza());
        assertEquals(state.getOrders(), res.getOrders());
        assertEquals(state.getStorage(), res.getStorage());
    }
    @Test
    public void testCustomerFactory() {
        var state = new State(100);
        var data = new CustomerData("John", new Order[] {new Order(0, "PIZZA"), new Order(-123, "Another one")});
        var factory = new DefaultCustomerFactory(state);
        var res = factory.construct(data);
        assertArrayEquals(new Order[] {new Order(0, "PIZZA"), new Order(-123, "Another one")}, res.getToPlace());
        assertEquals(state.getOrders(), res.getOrders());
    }
    @Test
    public void testDelivererFactory() {
        var state = new State(100);
        DelivererData data = new DelivererData("John", 82);
        var factory = new DefaultDelivererFactory(state);
        var res = factory.construct(data);
        assertEquals(82, res.getDeliveryTime());
        assertEquals(state.getStorage(), res.getStorage());
    }
}
