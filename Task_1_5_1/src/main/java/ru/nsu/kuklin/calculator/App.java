package ru.nsu.kuklin.calculator;

import java.util.*;

/**
 * Stub docs.
 */
public class App {
    /**
     * main.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            var input = scanner.nextLine();
            System.out.println("input: " + input);
            var res = Calculator.execute(input);
            if (res.isRight()) {
                System.out.println(res.getRight().get());
                return;
            }

            System.out.println(res.getLeft().get());
        }
    }
}


