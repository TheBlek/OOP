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
    public void testBakerFactoryMap() {
        var state = new State(100);
        var data = new BakerData[]{
                new BakerData("John", 82),
                new BakerData("Mike", -12),
                new BakerData("Авдотьевна", 0),
        };
        var factory = new DefaultBakerFactory(state);
        var res = factory.map(data);
        for (int i = 0; i < res.size(); i++) {
            assertEquals(data[i].timePerPizza(), res.get(i).getTimeToPizza());
            assertEquals(state.getOrders(), res.get(i).getOrders());
            assertEquals(state.getStorage(), res.get(i).getStorage());
        }
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
    public void testCustomerFactoryMap() {
        var state = new State(100);
        var data = new CustomerData[]{
                new CustomerData("John", new Order[] {new Order(0, "PIZZA"), new Order(-123, "Another one")}),
                new CustomerData("Mike", new Order[] {new Order(1, "PIZZA1"), new Order(-122, "Another oneasdfasd")}),
                new CustomerData("Авдотьевна", new Order[] {new Order(2, "PIZZA2"), new Order(-0, "Another onesdf")}),
        };
        var factory = new DefaultCustomerFactory(state);
        var res = factory.map(data);
        for (int i = 0; i < res.size(); i++) {
            assertArrayEquals(data[i].orders(), res.get(i).getToPlace());
            assertEquals(state.getOrders(), res.get(i).getOrders());
        }
    }
    @Test
    public void testDelivererFactory() {
        var state = new State(100);
        var data = new DelivererData("John", 82);
        var factory = new DefaultDelivererFactory(state);
        var res = factory.construct(data);
        assertEquals(82, res.getDeliveryTime());
        assertEquals(state.getStorage(), res.getStorage());
    }
    @Test
    public void testDelivererFactoryMap() {
        var state = new State(100);
        var data = new DelivererData[]{
                new DelivererData("John", 90),
                new DelivererData("Mike", -42),
                new DelivererData("Авдотьевна", 0),
        };
        var factory = new DefaultDelivererFactory(state);
        var res = factory.map(data);
        for (int i = 0; i < res.size(); i++) {
            assertEquals(data[i].deliveryTime(), res.get(i).getDeliveryTime());
            assertEquals(state.getStorage(), res.get(i).getStorage());
        }
    }
}
