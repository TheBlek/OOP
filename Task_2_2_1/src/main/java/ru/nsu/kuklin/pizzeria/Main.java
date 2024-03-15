package ru.nsu.kuklin.pizzeria;

import ru.nsu.kuklin.pizzeria.baker.Baker;
import ru.nsu.kuklin.pizzeria.baker.BakerData;
import ru.nsu.kuklin.pizzeria.baker.DefaultBakerFactory;
import ru.nsu.kuklin.pizzeria.customer.CustomerData;
import ru.nsu.kuklin.pizzeria.customer.DefaultCustomerFactory;
import ru.nsu.kuklin.pizzeria.deliverer.DefaultDelivererFactory;
import ru.nsu.kuklin.pizzeria.deliverer.Deliverer;
import ru.nsu.kuklin.pizzeria.deliverer.DelivererData;
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

/**
 * Main class that runs pizzeria in terminal.
 */
public class Main {

    /**
     * Runs pizzeria on sample config until quit in written into terminal.
     */
    public static void main(String[] args) throws IOException {
        var pizza = new Pizzeria(
                "configs/defaultStorage.json",
                "configs/bakers1.json",
                "configs/deliverers1.json",
                "configs/customers1.json");
        pizza.run(new ExitCondition() {
            @Override
            public void waitForExitCondition() {
                Scanner input = new Scanner(System.in);
                String str;
                do {
                    try {
                        str = input.nextLine();
                    } catch (Exception e) {
                        break;
                    }
                } while(str != null && !str.equals("quit"));
            }
        });
    }
}