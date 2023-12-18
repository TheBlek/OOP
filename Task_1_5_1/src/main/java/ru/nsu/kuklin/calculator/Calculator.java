package ru.nsu.kuklin.calculator;

import java.util.*;
import java.util.function.*;
import java.lang.*;
import java.text.*;
import io.jbock.util.*;

/**
 * Class providing calculator interface.
 */
public class Calculator {
    /**
     * Calculates the expression passed as input.
     * On success, returns left variant with the result.
     * On failure, returns right variant with error message.
     */
    public static Either<Complex, String> execute(String input) {
        var lexems = input.split(" ");
        
        Complex[] stack = new Complex[lexems.length];
        int stackSize = 0;

        var ops = new Operation[] {
            new Operation("+", (args) -> Either.left(Complex.add(args[0], args[1])), 2),
            new Operation("*", (args) -> Either.left(Complex.mul(args[0], args[1])), 2),
            new Operation("-", (args) -> Either.left(Complex.sub(args[0], args[1])), 2),
            new Operation(
                "/", 
                (args) -> {
                    if (args[1].lenSqr() > 0.0001) {
                        return Either.left(Complex.div(args[0], args[1]));
                    } else {
                        return Either.right("Division by zero");
                    }
                },
                2
            ),
            new Operation(
                "ln",
                (args) -> {
                    if (args[0].lenSqr() > 0.0001) {
                        return Either.left(Complex.log(args[0]));
                    } else {
                        return Either.right("Logarithm of zero number");
                    }
                },
                1
            ),
            new Operation(
                "pow",
                (args) -> Either.left(Complex.exp(Complex.mul(args[1], Complex.log(args[0])))),
                2
            ),
            new Operation("sin", (args) -> Either.left(Complex.sin(args[0])), 1),
            new Operation("cos", (args) -> Either.left(Complex.cos(args[0])), 1),
            new Operation(
                "sqrt",
                (args) -> Either.left(Complex.sqrt(args[0])),
                1
            ),
            new Operation(
                "sind",
                (args) -> {
                    if (args[0].y() == 0) {
                        return Either.left(new Complex(Math.sin(Math.toRadians(args[0].x())), 0));
                    } else {
                        return Either.right("Failed to convert imaginary number to degrees");
                    }
                },
                1
            ),
            new Operation(
                "cosd",
                (args) -> {
                    if (args[0].y() == 0) {
                        return Either.left(new Complex(Math.cos(Math.toRadians(args[0].x())), 0));
                    } else {
                        return Either.right("Failed to convert imaginary number to degrees");
                    }
                },
                1
            ),
        };

        for (int i = lexems.length - 1; i >= 0; i--) {
            try {
                double result = Double.parseDouble(lexems[i]); 
                stack[stackSize] = new Complex(result, 0);
                stackSize++;
                continue;
            } catch (NumberFormatException e) {
                i = i; // Linter is very notorious being, y'know
            }
            if (lexems[i].charAt(lexems[i].length() - 1) == 'i') {
                try {
                    double result = 1;
                    if (lexems[i].length() > 1) { 
                        result = Double.parseDouble(lexems[i].substring(0, lexems[i].length() - 1));
                    }

                    stack[stackSize] = new Complex(0, result);
                    stackSize++;
                    continue;
                } catch (NumberFormatException e1) {
                    i = i; // Linter is very notorious being, y'know
                }
            }
            int k = 0;
            while (k < ops.length && !ops[k].match(lexems[i])) {
                k++;
            }
            if (k == ops.length) {
                return Either.right("Unrecognised operator: \"" + lexems[i] + "\"");
            }

            Complex[] args = new Complex[ops[k].getArity()];
            for (int j = 0; j < args.length; j++) {
                if (stackSize == 0) {
                    return Either.right("Unbalanced operator \"" + ops[k] + "\" at pos " + i);
                }
                args[j] = stack[stackSize - 1];
                stackSize--;
            }
            var res = ops[k].call(args);
            if (res.isLeft()) {
                Complex value = res.getLeft().get();
                stack[stackSize] = value;
                stackSize++;
            } else {
                return Either.right("Operation \"" + ops[k] + "\" failed: " + res.getRight().get());
            }
        }
        if (stackSize != 1) {
            return Either.right("Unbalanced expression");
        }
        return Either.left(stack[0]);
    }

    private static class Operation {
        Operation(String p, Function<Complex[], Either<Complex, String>> f, int r) {
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

        public Either<Complex, String> call(Complex[] args) {
            assert args.length == arity : "Wrong number of arguments";
            return operation.apply(args);
        }

        public String toString() {
            return pattern;
        }
         
        private String pattern;
        private Function<Complex[], Either<Complex, String>> operation;
        private int arity;
    }
}

