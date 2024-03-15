package ru.nsu.kuklin.pizzeria;

import ru.nsu.kuklin.pizzeria.baker.BakerData;
import ru.nsu.kuklin.pizzeria.baker.DefaultBakerFactory;
import ru.nsu.kuklin.pizzeria.customer.CustomerData;
import ru.nsu.kuklin.pizzeria.customer.DefaultCustomerFactory;
import ru.nsu.kuklin.pizzeria.deliverer.DefaultDelivererFactory;
import ru.nsu.kuklin.pizzeria.deliverer.DelivererData;
import ru.nsu.kuklin.pizzeria.io.JsonDeserializer;
import ru.nsu.kuklin.pizzeria.worker.Worker;
import ru.nsu.kuklin.pizzeria.worker.WorkerProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs pizzeria based on given configs.
 */
public class Pizzeria {
    /**
     * Construct pizzeria from given configs.
     */
    public Pizzeria(
        String storageFile,
        String bakersFile,
        String deliverersFile,
        String customersFile) {
        this.storageFile = storageFile;
        this.bakersFile = bakersFile;
        this.deliverersFile = deliverersFile;
        this.customersFile = customersFile;
    }

    /**
     *  Runs pizzeria until exit condition is met.
     *  Once closed, all existing orders in pizzeria are finished and delivered.
     */
    public void run(ExitCondition exit) {
        Integer[] lengths = new JsonDeserializer<>(Integer.class, new File(storageFile)).read();
        state = new State(lengths[0], lengths[1]);
        var bakers = startWorkers(
            new WorkerProvider<>(
                new JsonDeserializer<>(BakerData.class, new File(bakersFile)),
                new DefaultBakerFactory(state))
        );
        var customers = startWorkers(
            new WorkerProvider<>(
                new JsonDeserializer<>(CustomerData.class, new File(customersFile)),
                new DefaultCustomerFactory(state))
        );
        var deliverers = startWorkers(
            new WorkerProvider<>(
                new JsonDeserializer<>(DelivererData.class, new File(deliverersFile)),
                new DefaultDelivererFactory(state))
        );
        exit.waitForExitCondition();
        // Stop taking orders
        for (var worker : customers) {
            worker.worker().stop();
        }
        for (var worker : customers) {
            try { worker.thread().join(); } catch (InterruptedException ignored) {}
        }
        // Stop baking when no left orders
        while (state.getOrders().getSize() > 0) {}
        for (var worker : bakers) {
            worker.worker().stop();
        }
        for (var worker : bakers) {
            try { worker.thread().join(); } catch (InterruptedException ignored) {}
        }
        // Stop delivering once no orders in storage
        while (state.getStorage().getSize() > 0) {}
        for (var worker : deliverers) {
            worker.worker().stop();
        }
        for (var worker : deliverers) {
            try { worker.thread().join(); } catch (InterruptedException ignored) {}
        }
    }

    /**
     * Get current pizzeria state.
     */
    public State getState() {
        return state;
    }

    private static <T extends Worker, D>
    List<RunningWorker> startWorkers(WorkerProvider<T, D> provider) {
        ArrayList<RunningWorker> result = new ArrayList<>();
        for (Worker w : provider.get()) {
            var t = new Thread(w);
            t.start();
            result.add(new RunningWorker(t, w));
        }
        return result;
    }

    private volatile State state;
    private final String bakersFile;
    private final String deliverersFile;
    private final String customersFile;
    private final String storageFile;
}
