package ru.nsu.kuklin.calculator;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.*;
import java.io.*;
import java.util.*;
import org.junit.jupiter.api.*;
import io.jbock.util.*;

/**
 * Stub docs.
 */
public class CalculatorTest {
    @Test
    public void testSum() {
        assertEquals(Either.left(new Complex(1, 2)), Calculator.execute("+ 1 2i"));
        assertEquals(Either.left(new Complex(1, 2)), Calculator.execute("+ 2i 1"));
    }

    @Test
    public void testSub() {
        assertEquals(Either.left(new Complex(1, -2)), Calculator.execute("- 1 2i"));
        assertEquals(Either.left(new Complex(-1, 2)), Calculator.execute("- 2i 1"));
    }
}

