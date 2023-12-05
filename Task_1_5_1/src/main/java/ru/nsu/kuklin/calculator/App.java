package ru.nsu.kuklin.calculator;

import java.util.*;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Calculator calc = new Calculator();
        while (true) {
            var res = calc.execute(scanner.nextLine());
            if (res.isRight()) {
                return;
            }

            System.out.println(res.getLeft().get());
        }
    }
}


