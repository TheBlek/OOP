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
        assertEquals(
            Either.left(new Complex(1, 2)),
            Calculator.execute("+ 1 2i")
        );
        assertEquals(
            Either.left(new Complex(1, 2)),
            Calculator.execute("+ 2i 1")
        );
    }

    @Test
    public void testSub() {
        assertEquals(
            Either.left(new Complex(1, -2)),
            Calculator.execute("- 1 2i")
        );
        assertEquals(
            Either.left(new Complex(-1, 2)),
            Calculator.execute("- 2i 1")
        );
    }

    @Test
    public void testMul() {
        assertEquals(
            Either.left(new Complex(0, 12)),
            Calculator.execute("* 6 2i")
        );
        assertEquals(
            Either.left(new Complex(-1, 0)),
            Calculator.execute("* i i")
        );
        assertEquals(
            Either.left(new Complex(23.04, -1.5950000000000006)),
            Calculator.execute("* + 1.01 2i - 4 9.5i")
        );
    }

    @Test
    public void testDiv() {
        assertEquals(
            Either.left(new Complex(1, 0)),
            Calculator.execute("/ i i")
        );
        assertEquals(
            Either.left(new Complex(0.15987973886292997, 0.9169666870284701)),
            Calculator.execute("/ + 2.2 8.12i - 9 0.83i")
        );
    }

    @Test
    public void testLog() {
        assertEquals(
            Either.left(new Complex(1.000331069627733, 0)),
            Calculator.execute("ln 2.719181918")
        );
        assertEquals(
            Either.left(new Complex(2.2014590648613295, -0.09196210022806267)),
            Calculator.execute("ln - 9 0.83i")
        );
        assertEquals(
            Either.left(new Complex(2.2014590648613295, 3.0496305533617303)),
            Calculator.execute("ln - 0.83i 9")
        );
    }

    @Test
    public void testPow() {
        assertEquals(
            Either.left(new Complex(1024, 0)),
            Calculator.execute("pow 2 10")
        );
        assertEquals(
            Either.left(new Complex(-114.7322267317185, -3186.2545485626397)),
            Calculator.execute("pow + 2 1.01i 10")
        );
    }

    @Test
    public void testSin() {
        assertEquals(
            Either.left(new Complex(9.265358966049026E-5, 0)),
            Calculator.execute("sin 3.1415")
        );
        assertEquals(
            Either.left(new Complex(1.2305680515396107, -0.23611652185091267)),
            Calculator.execute("sin + 8.15 0.74i")
        );
    }

    @Test
    public void testCos() {
        assertEquals(
            Either.left(new Complex(-0.9999999957076562, 0)),
            Calculator.execute("cos 3.1415")
        );
        assertEquals(
            Either.left(new Complex(-0.3752973659055549, -0.7742059354168435)),
            Calculator.execute("cos + 8.15 0.74i")
        );
    }

    @Test
    public void testSqrt() {
        assertEquals(
            Either.left(new Complex(1.772427713617681, 0)),
            Calculator.execute("sqrt 3.1415")
        );
        assertEquals(
            Either.left(new Complex(1.8033841761788165, -0.3327078100858895)),
            Calculator.execute("sqrt - 3.1415 1.2i")
        );
    }

    @Test
    public void testDegrees() {
        assertEquals(
            Either.left(new Complex(1.0, 0)),
            Calculator.execute("sind 90")
        );
        assertEquals(
            Either.left(new Complex(6.123233995736766E-17, 0)),
            Calculator.execute("cosd 90")
        );
        assertEquals(
            Either.left(new Complex(-1.0, 0)),
            Calculator.execute("cosd 180")
        );
    }

    @Test
    public void integrationTest() {
        assertEquals(
            Either.left(new Complex(5.897713069869024, -1.0385882044932546)),
            Calculator.execute("ln * / sqrt - 3.2i 1.5 + 1.01i 1.1 pow sin + 2 8i cos + 1 i")
        );
    }

    @Test
    public void testUnbalanced() {
        assertEquals(
            Either.right("Unbalanced operator \"*\" at pos 1"),
            Calculator.execute("ln * pow sin + 2 8i cos + 1 i")
        );
        assertEquals(
            Either.right("Unbalanced expression"),
            Calculator.execute("ln i ln * + 1 i pow sin + 2 8i cos + 1 i")
        );
    }

    @Test
    public void testUnrecognised() {
        assertEquals(
            Either.right("Unrecognised operator: \"asldj\""),
            Calculator.execute("ln * asldj sin + 2 8i cos + 1 i")
        );
    }

    @Test
    public void divisionByZero() {
        assertEquals(
            Either.right("Operation \"/\" failed: Division by zero"),
            Calculator.execute("/ cos + 1 2i sind 0")
        );
    }

    @Test
    public void logarithmOfZero() {
        assertEquals(
            Either.right("Operation \"ln\" failed: Logarithm of zero number"),
            Calculator.execute("ln sind 0")
        );
    }

    @Test
    public void complexDegrees() {
        assertEquals(
            Either.right(
                "Operation \"sind\" failed: Failed to convert imaginary number to degrees"
            ),
            Calculator.execute("sind pow 2.7 + 1 i")
        );
        assertEquals(
            Either.right(
                "Operation \"cosd\" failed: Failed to convert imaginary number to degrees"
            ),
            Calculator.execute("cosd pow 2.7 + 1 i")
        );
    }
}

