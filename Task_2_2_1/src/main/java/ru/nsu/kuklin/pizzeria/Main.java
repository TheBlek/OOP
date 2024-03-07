package ru.nsu.kuklin.pizzeria;

import ru.nsu.kuklin.pizzeria.baker.BakerData;
import ru.nsu.kuklin.pizzeria.baker.DefaultBakerFactory;
import ru.nsu.kuklin.pizzeria.io.IDeserializer;
import ru.nsu.kuklin.pizzeria.worker.Worker;
import ru.nsu.kuklin.pizzeria.worker.WorkerFactory;
import ru.nsu.kuklin.pizzeria.worker.WorkerProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static State state;

    public static void main(String[] args) throws IOException {
        state = new State(100);
        ArrayList<Thread> workers = new ArrayList<>();
        WorkerProvider[] providers = {
                new WorkerProvider<>(new IDeserializer<BakerData>() {
                    @Override
                    public BakerData[] read() {
                        return new BakerData[] {new BakerData("Smith", 10)};
                    }
                }, new DefaultBakerFactory(state))
        };
        for (var provider : providers) {
            workers.addAll((List<Thread>) provider
                    .get()
                    .stream()
                    .map((w) -> {
                        var t = new Thread((Worker) w);
                        t.start();
                        return t;
                    })
                    .collect(Collectors.toList())
            );
        }
        // Exit condition
        for (var worker : workers) {
            worker.interrupt();
        }
    }
}