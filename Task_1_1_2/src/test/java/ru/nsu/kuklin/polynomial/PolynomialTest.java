
package ru.nsu.kuklin.polynomial;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import org.junit.jupiter.api.Test;

class PolynomialTest {
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
    void testNegDiff() {
        Polynomial p = new Polynomial(new int[] {1290, 1, 2, 5, 3});
        assertEquals(null, p.differentiate(-123));
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
    void testMinusEdgecases() {
        Polynomial p = new Polynomial(new int[] {1290, 1, 2, 5, -3, 0});
        Polynomial p1 = new Polynomial(new int[] {0, 0, 0});
        assertEquals(p.minus(p), new Polynomial(new int[] {}));
        assertEquals(p1.minus(p1), new Polynomial(new int[] {}));
        assertEquals(p.minus(p1), new Polynomial(new int[] {1290, 1, 2, 5, -3}));
        assertEquals(p1.minus(p), new Polynomial(new int[] {-1290, -1, -2, -5, 3}));
    }

    @Test
    void testArithmeticWithBigger() {
        Polynomial p1 = new Polynomial(new int[] {1290, 1, 2,});
        Polynomial p2 = new Polynomial(new int[] {1290, 1, 2, 5, 3, 10});

        assertEquals(p1.plus(p2), new Polynomial(new int[] {2580, 2, 4, 5, 3, 10}));
        assertEquals(p1.minus(p2), new Polynomial(new int[] {0, 0, 0, -5, -3, -10}));
    }

    @Test
    void testEqualityReflexivity() {
        int size = 10000;
        int[] coeffs = new int[size];
        Random rng = new Random();
        
        for (int i = 0; i < size; i++) {
            coeffs[i] = rng.nextInt();
        }
        Polynomial p1 = new Polynomial(coeffs.clone());
        Polynomial p2 = new Polynomial(coeffs.clone());

        assertEquals(p1, p2);
    }

    @Test
    void testEqualityTransitivity() {
        // This IS a really dumb test but that's the smartest I've got
        // that tests the property for real and not just three equal polynomials
        int size = 5;
        int maxNum = 10;
        int positiveTestCount = 5;
        int[] coeffs1 = new int[size];
        int[] coeffs2 = new int[size];
        int[] coeffs3 = new int[size];
        Random rng = new Random();
        
        for (int j = 0; j < positiveTestCount;) {
            for (int i = 0; i < size; i++) {
                coeffs1[i] = rng.nextInt() % maxNum;
                coeffs2[i] = rng.nextInt() % maxNum;
                coeffs3[i] = rng.nextInt() % maxNum;
            }
            Polynomial p1 = new Polynomial(coeffs1.clone());
            Polynomial p2 = new Polynomial(coeffs2.clone());
            Polynomial p3 = new Polynomial(coeffs2.clone());

            assertEquals(p1.equals(p2) && p2.equals(p3), p1.equals(p3));
            if (p1.equals(p2) && p2.equals(p3)) {
                j++;
            }
        }
    }

    @Test
    void testEqualitySymmetry() {
        int size = 10000;
        int[] coeffs1 = new int[size];
        int[] coeffs2 = new int[size];
        Random rng = new Random();
        
        for (int i = 0; i < size; i++) {
            coeffs1[i] = rng.nextInt();
            coeffs2[i] = rng.nextInt();
        }
        Polynomial p1 = new Polynomial(coeffs1.clone());
        Polynomial p2 = new Polynomial(coeffs2.clone());

        assertEquals(p1.equals(p2), p2.equals(p1));
    }

    @Test
    void testEqualityHashcode() {
        int size = 10000;
        int[] coeffs1 = new int[size];
        int[] coeffs2 = new int[size];
        Random rng = new Random();
        
        for (int j = 0; j < 1000; j++) {
            for (int i = 0; i < size; i++) {
                coeffs1[i] = rng.nextInt();
                coeffs2[i] = rng.nextInt();
            }
            Polynomial p1 = new Polynomial(coeffs1.clone());
            Polynomial p2 = new Polynomial(coeffs2.clone());

            assertTrue(p1.hashCode() == p2.hashCode() || !p2.equals(p1));
        }
    }

    @Test
    void testEqualitySaneness() {
        int size = 10000;
        int[] coeffs = new int[size];
        Random rng = new Random();
        
        for (int i = 0; i < size; i++) {
            coeffs[i] = rng.nextInt();
        }
        Polynomial p1 = new Polynomial(coeffs.clone());

        assertNotEquals(p1, coeffs);
        assertNotEquals(p1, coeffs.clone());
        assertNotEquals(p1, null);
    }
}
