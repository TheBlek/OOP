package ru.nsu.kuklin.pizzeria;

import java.util.*;

/**
 * Main class that runs pizzeria in terminal.
 */
public class Main {

    /**
     * Runs pizzeria on sample config until quit in written into terminal.
     */
    public static void main(String[] args) {
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
                } while (str != null && !str.equals("quit"));
            }
        });
    }
}