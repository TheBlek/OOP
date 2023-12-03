package ru.nsu.kuklin.calculator;

import java.util.*;
import java.util.function.*;
import java.lang.*;
import java.text.*;
import io.jbock.util.*;

public class Calculator {
    public void run() {
        Scanner scanner = new Scanner(System.in);

        var input = scanner.nextLine();
        var lexems = input.split(" ");
        
        double[] stack = new double[lexems.length];
        int stackSize = 0;

        var ops = new Operation[] {
            new Operation("+", (args) -> Either.left(args[0] + args[1]), () -> 2),
            new Operation("*", (args) -> Either.left(args[0] * args[1]), () -> 2),
            new Operation("-", (args) -> Either.left(args[0] - args[1]), () -> 2),
            new Operation(
                "/", 
                (args) -> {
                    if (args[1] != 0)
                        return Either.left(args[0] / args[1]);
                    else 
                        return Either.right("Division by zero");
                },
                () -> 2
            ),
            new Operation("sin", (args) -> Either.left(Math.sin(args[0])), () -> 1),
            new Operation("cos", (args) -> Either.left(Math.cos(args[0])), () -> 1),
            new Operation(
                "ln",
                (args) -> {
                    if (args[0] > 0)
                        return Either.left(Math.log(args[0]));
                    else
                        return Either.right("Logarithm of non-positive number");
                },
                () -> 1
            ),
            new Operation(
                "pow",
                (args) -> {
                    if (Math.floor(args[1]).equals(args[1]) || args[0] >= 0)
                        return Either.left(Math.pow(args[0], args[1]));
                    else
                        return Either.right("Raising negative number to non-integral power");
                },
                () -> 2
            ),
        };

        for (int i = lexems.length - 1; i >= 0; i--) {
            double result;
            try {
                result = Double.parseDouble(lexems[i]); 
                stack[stackSize] = result;
                stackSize++;
            } catch (NumberFormatException e) {
                int k = 0; while (k < ops.length && !ops[k].match(lexems[i])) {
                    k++;
                }
                if (k < ops.length) {
                    double[] args = new double[ops[k].getArity()];
                    for (int j = 0; j < args.length; j++) {
                        if (stackSize == 0) {
                            System.out.println("Unbalanced operator \"" + ops[k] + "\" at pos " + i);
                            return;
                        }
                        args[j] = stack[stackSize-1];
                        stackSize--;
                    }
                    var res = ops[k].call(args);
                    if (res.isLeft()) {
                        double value = res.getLeft().get();
                        stack[stackSize] = value;
                        stackSize++;
                    } else {
                        System.out.println("Operation \"" + ops[k] + "\" failed: " + res.getRight().get());
                    }
                } else {
                    System.out.println("Parse exception at pos " + i);
                    return;
                }
            }
        }
        for (int i = 0; i < stackSize; i++) {
            System.out.println(stack[i]);
        }
    }

    private static class Operation {
        Operation(String p, Function<double[], Either<Double, String>> f, IntSupplier r) {
            pattern = p;
            operation = f;
            arity = r;
        }

        public boolean match(String lexem) {
            return pattern.equals(lexem);
        }

        public int getArity() {
            return arity.getAsInt();
        }

        public Either<Double, String> call(double[] args) {
            assert args.length == arity.getAsInt() : "Wrong number of arguments";
            return operation.apply(args);
        }

        public String toString() {
            return pattern;
        }
         
        private String pattern;
        private Function<double[], Either<Double, String>> operation;
        private IntSupplier arity;
    }
}

