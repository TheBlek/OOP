package ru.nsu.kuklin.calculator;

import java.util.*;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Calculator calc = new Calculator();
        while (true) {
            var input = scanner.nextLine();
            System.out.println("input: " + input);
            var res = calc.execute(input);
            if (res.isRight()) {
                System.out.println(res.getRight().get());
                return;
            }

            System.out.println(res.getLeft().get());
        }
    }
}


