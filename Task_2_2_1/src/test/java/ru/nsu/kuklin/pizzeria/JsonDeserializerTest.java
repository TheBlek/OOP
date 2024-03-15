package ru.nsu.kuklin.pizzeria;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import ru.nsu.kuklin.pizzeria.baker.BakerData;
import ru.nsu.kuklin.pizzeria.customer.CustomerData;
import ru.nsu.kuklin.pizzeria.io.JsonDeserializer;
import java.io.File;


/**
 * Tests for json deserialization.
 */
public class JsonDeserializerTest {
    @Test
    public void testBakers() {
        var ds = new JsonDeserializer<>(BakerData.class, new File("configs/bakers1.json"));
        var res = ds.read();
        assertArrayEquals(new BakerData[] {
            new BakerData("Smith", 10.f),
            new BakerData("Cvbn", 15.f),
        }, res);
    }

    @Test
    public void testCustomers() {
        var ds = new JsonDeserializer<>(CustomerData.class, new File("configs/customers1.json"));
        var res = ds.read();
        var data = new CustomerData[] {
            new CustomerData("John", new Order[] {
                new Order(0, "Pizza"),
                new Order(1, "Big pizza"),
            }),
        };
        assertEquals(data.length, res.length);
        for (int i = 0; i < data.length; i++) {
            assertEquals(data[i].name(), res[i].name());
            assertArrayEquals(data[i].orders(), res[i].orders());
        }
    }
}