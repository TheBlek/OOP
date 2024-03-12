package ru.nsu.kuklin.pizzeria;

import ru.nsu.kuklin.pizzeria.baker.Baker;
import ru.nsu.kuklin.pizzeria.baker.BakerData;
import ru.nsu.kuklin.pizzeria.baker.DefaultBakerFactory;
import ru.nsu.kuklin.pizzeria.customer.CustomerData;
import ru.nsu.kuklin.pizzeria.customer.DefaultCustomerFactory;
import ru.nsu.kuklin.pizzeria.io.IDeserializer;
import ru.nsu.kuklin.pizzeria.io.JsonDeserializer;
import ru.nsu.kuklin.pizzeria.worker.Worker;
import ru.nsu.kuklin.pizzeria.worker.WorkerFactory;
import ru.nsu.kuklin.pizzeria.worker.WorkerProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static State state;
    private static ArrayList<Thread> workers;

    public static void main(String[] args) throws IOException {
        state = new State(100);
        workers = new ArrayList<>();
        startWorkers(
                new WorkerProvider<>(
                        new JsonDeserializer<>(BakerData.class, new File("bakers1.json")),
                        new DefaultBakerFactory(state))
        );
        startWorkers(
                new WorkerProvider<>(
                        new JsonDeserializer<>(CustomerData.class, new File("customers1.json")),
                        new DefaultCustomerFactory(state))
        );
        Scanner input = new Scanner(System.in);
        String in;
        do {
            try {
                in = input.nextLine();
            } catch (Exception e) {
                break;
            }
        } while(in != null && !in.equals("quit"));

        // Exit condition
        for (var worker : workers) {
            worker.interrupt();
        }
    }

    private static <T extends Worker, D> void startWorkers(WorkerProvider<T, D> provider) {
        for (Worker w : provider.get()) {
            var t = new Thread(w);
            t.start();
            workers.add(t);
        }
    }
}