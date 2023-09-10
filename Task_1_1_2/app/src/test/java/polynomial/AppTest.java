/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package polynomial;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    @Test 
    void testFromDef() {
        Polynomial p1 = new Polynomial(new int[] {4, 3, 6, 7});
        Polynomial p2 = new Polynomial(new int[] {3, 2, 8});
        assertTrue(p1.plus(p2.differentiate(1)).toString().equals("7x^3 + 6x^2 + 19x + 6"));
        assertTrue(p1.times(p2).evaluate(2) == 3510);
    }

    @Test 
    void testLongDiff() {
        Polynomial p = new Polynomial(new int[] {1290, 1, 2, 5, 3});
        assertTrue(p.differentiate(3).toString().equals("72x + 30"));
    }

    @Test 
    void testDiffIntoOblivion() {
        Polynomial p = new Polynomial(new int[] {1290, 1, 2, 5, 3});
        assertTrue(p.differentiate(15).toString().equals(""));
    }

    @Test 
    void testEquals() {
        Polynomial p = new Polynomial(new int[] {1290, 1, 2, 5, 3});
        assertTrue(p.equals(new Polynomial(new int[] {1290, 1, 2, 5, 3})));
        assertTrue(p.differentiate(3).equals(new Polynomial(new int[] {30, 72})));
    }

    @Test
    void testMinus() {
        Polynomial p = new Polynomial(new int[] {1290, 1, 2, 5, 3});
        Polynomial p1 = new Polynomial(new int[] {0, 1, 2, 5, 3});
        assertTrue(p.minus(p).equals(new Polynomial(new int[] {})));
        assertTrue(p.minus(p1).equals(new Polynomial(new int[] {1290})));
    }

    @Test
    void testArithmeticWithBigger() {
        Polynomial p1 = new Polynomial(new int[] {1290, 1, 2, 5, 3});
        Polynomial p2 = new Polynomial(new int[] {1290, 1, 2, 5, 3, 10});

        assertTrue(p1.plus(p2).equals(new Polynomial(new int[] {2580, 2, 4, 10, 6, 10})));
        assertTrue(p1.minus(p2).equals(new Polynomial(new int[] {0, 0, 0, 0, 0, -10})));
    }
}
