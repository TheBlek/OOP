package ru.nsu.kuklin.pizzeria;

import org.junit.jupiter.api.Test;
import ru.nsu.kuklin.pizzeria.baker.BakerData;
import ru.nsu.kuklin.pizzeria.baker.DefaultBakerFactory;
import ru.nsu.kuklin.pizzeria.io.IDeserializer;
import ru.nsu.kuklin.pizzeria.worker.WorkerProvider;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProviderTest {
    @Test
    public void testBakerProvider() {
        var state = new State(10, 10);
        var factory = new DefaultBakerFactory(state);
        var data = new BakerData[]{
            new BakerData("John", 82),
            new BakerData("Mike", -12),
            new BakerData("Авдотьевна", 0),
        };
        var provider = new WorkerProvider<>(new IDeserializer<BakerData>() {
            @Override
            public BakerData[] read() {
                return data;
            }
        }, factory);
        var res = provider.get();
        for (int i = 0; i < data.length; i++) {
            assertEquals(data[i].timePerPizza(), res.get(i).getTimeToPizza());
            assertEquals(state.getOrders(), res.get(i).getOrders());
            assertEquals(state.getStorage(), res.get(i).getStorage());
        }
    }
}