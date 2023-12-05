package ru.nsu.kuklin.calculator;

import java.util.*;
import java.util.function.*;
import java.lang.*;
import java.text.*;
import io.jbock.util.*;

public class Calculator {
    public Either<Double, String> execute(String input) {
        var lexems = input.split(" ");
        
        double[] stack = new double[lexems.length];
        int stackSize = 0;

        var ops = new Operation[] {
            new Operation("+", (args) -> Either.left(args[0] + args[1]), 2),
            new Operation("*", (args) -> Either.left(args[0] * args[1]), 2),
            new Operation("-", (args) -> Either.left(args[0] - args[1]), 2),
            new Operation(
                "/", 
                (args) -> {
                    if (args[1] != 0)
                        return Either.left(args[0] / args[1]);
                    else 
                        return Either.right("Division by zero");
                },
                2
            ),
            new Operation("sin", (args) -> Either.left(Math.sin(args[0])), 1),
            new Operation("cos", (args) -> Either.left(Math.cos(args[0])), 1),
            new Operation(
                "ln",
                (args) -> {
                    if (args[0] > 0)
                        return Either.left(Math.log(args[0]));
                    else
                        return Either.right("Logarithm of non-positive number");
                },
                1
            ),
            new Operation(
                "pow",
                (args) -> {
                    if (Math.floor(args[1]) == args[1] || args[0] >= 0)
                        return Either.left(Math.pow(args[0], args[1]));
                    else
                        return Either.right("Raising negative number to non-integral power");
                },
                2
            ),
            new Operation(
                "sqrt",
                (args) -> {
                    if (args[0] >= 0)
                        return Either.left(Math.sqrt(args[0]));
                    else
                        return Either.right("Root of a negative number");
                },
                1
            ),
        };

        for (int i = lexems.length - 1; i >= 0; i--) {
            double result;
            try {
                result = Double.parseDouble(lexems[i]); 
                stack[stackSize] = result;
                stackSize++;
            } catch (NumberFormatException e) {
                int k = 0;
                while (k < ops.length && !ops[k].match(lexems[i])) {
                    k++;
                }
                if (k == ops.length) {
                    return Either.right("Unrecognised operator: \"" + lexems[i] + "\"");
                }

                double[] args = new double[ops[k].getArity()];
                for (int j = 0; j < args.length; j++) {
                    if (stackSize == 0) {
                        return Either.right("Unbalanced operator \"" + ops[k] + "\" at pos " + i);
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
                    return Either.right("Operation \"" + ops[k] + "\" failed: " + res.getRight().get());
                }
            }
        }
        if (stackSize != 1) {
            return Either.right("Unbalanced expression");
        }
        return Either.left(stack[0]);
    }

    private static class Operation {
        Operation(String p, Function<double[], Either<Double, String>> f, int r) {
            pattern = p;
            operation = f;
            arity = r;
        }

        public boolean match(String lexem) {
            return pattern.equals(lexem);
        }

        public int getArity() {
            return arity;
        }

        public Either<Double, String> call(double[] args) {
            assert args.length == arity : "Wrong number of arguments";
            return operation.apply(args);
        }

        public String toString() {
            return pattern;
        }
         
        private String pattern;
        private Function<double[], Either<Double, String>> operation;
        private int arity;
    }
}

