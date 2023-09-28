
package ru.nsu.kuklin.polynomial;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AppTest {
    @Test 
    void testFromDef() {
        Polynomial p1 = new Polynomial(new int[] {4, 3, 6, 7});
        Polynomial p2 = new Polynomial(new int[] {3, 2, 8});
        assertEquals("7x^3 + 6x^2 + 19x + 6", p1.plus(p2.differentiate(1)).toString());
        assertEquals(3510, p1.times(p2).evaluate(2));
    }

    @Test 
    void testLongDiff() {
        Polynomial p = new Polynomial(new int[] {1290, 1, 2, 5, 3});
        assertEquals("72x + 30", p.differentiate(3).toString());
    }

    @Test 
    void testDiffIntoOblivion() {
        Polynomial p = new Polynomial(new int[] {1290, 1, 2, 5, 3});
        assertEquals("", p.differentiate(15).toString());
    }

    @Test 
    void testEquals() {
        Polynomial p = new Polynomial(new int[] {1290, 1, 2, 5, 3});
        assertEquals(p, new Polynomial(new int[] {1290, 1, 2, 5, 3}));
        assertEquals(p.differentiate(3), new Polynomial(new int[] {30, 72}));
    }

    @Test
    void testMinus() {
        Polynomial p = new Polynomial(new int[] {1290, 1, 2, 5, 3});
        Polynomial p1 = new Polynomial(new int[] {0, 1, 2, 5, 3});
        assertEquals(p.minus(p), new Polynomial(new int[] {}));
        assertEquals(p.minus(p1), new Polynomial(new int[] {1290}));
    }

    @Test
    void testArithmeticWithBigger() {
        Polynomial p1 = new Polynomial(new int[] {1290, 1, 2, 5, 3});
        Polynomial p2 = new Polynomial(new int[] {1290, 1, 2, 5, 3, 10});

        assertEquals(p1.plus(p2), new Polynomial(new int[] {2580, 2, 4, 10, 6, 10}));
        assertEquals(p1.minus(p2), new Polynomial(new int[] {0, 0, 0, 0, 0, -10}));
    }
}