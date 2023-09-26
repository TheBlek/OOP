
package ru.nsu.kuklin.polynomial;

import java.util.Arrays;

/**
 * Represents and manipulates a mathematical polynomial.
 *
 */
public class Polynomial {
    public int[] coeffs;

    /**
     * Create a polynomial with given coefficients.
     *
     * @param cs Coefficients for the polynomial: a0 + a1*x^1 + ... + an*x^n
     * Power of x for coeff is equal to coeff's index in an array
     */
    public Polynomial(int[] cs) {
        coeffs = cs;
    }

    private void trim() {
        int lastEmpty = coeffs.length;
        while (lastEmpty > 0 && coeffs[lastEmpty - 1] == 0) {
            lastEmpty--;
        }
        int[] trimmed = new int[lastEmpty];
        System.arraycopy(coeffs, 0, trimmed, 0, lastEmpty);
        coeffs = trimmed;
    }

    /**
     * Evaluate polynomial at a given point.
     *
     * @param x Point to evaluate at
     */
    public double evaluate(double x) {
        double res = 0;
        for (int i = 0; i < coeffs.length; i++) {
            res += coeffs[i] * Math.pow(x, i);
        }
        return res;
    }

    /**
     * Create a new polynomial resulting from an addition.
     *
     * @param rhs Polynomial to add with
     */
    public Polynomial plus(Polynomial rhs) {
        int[] summed;
        int[] other;
        if (coeffs.length >= rhs.coeffs.length) {
            summed = coeffs.clone();
            other = rhs.coeffs;
        } else {
            summed = rhs.coeffs.clone();
            other = coeffs;
        }

        for (int i = 0; i < other.length; i++) {
            summed[i] += other[i]; 
        }

        Polynomial res = new Polynomial(summed);
        res.trim();

        return res;
    }

    /**
     * Create a new polynomial resulting from a subtraction.
     *
     * @param rhs Polynomial to subtract from caller
     */
    public Polynomial minus(Polynomial rhs) {
        int[] diff;
        if (coeffs.length >= rhs.coeffs.length) {
            diff = new int[coeffs.length];
        } else {
            diff = new int[rhs.coeffs.length];
        }
        System.arraycopy(coeffs, 0, diff, 0, coeffs.length); 

        for (int i = 0; i < rhs.coeffs.length; i++) {
            diff[i] -= rhs.coeffs[i];
        }

        Polynomial res = new Polynomial(diff);
        res.trim();

        return res;
    }

    /**
     * Create a new polynomial resulting from a multiplication.
     *
     * @param rhs Polynomial to multiply with
     */
    public Polynomial times(Polynomial rhs) {
        int[] mul = new int[rhs.coeffs.length + coeffs.length];

        for (int i = 0; i < coeffs.length; i++) {
            for (int j = 0; j < rhs.coeffs.length; j++) {
                mul[i + j] += coeffs[i] * rhs.coeffs[j];
            }
        }

        Polynomial res = new Polynomial(mul);
        res.trim();

        return res;
    }

    /**
     * Create a new polynomial resulting from taking a power-th derivative.
     *
     * @param power Number of derivatives to take
     */
    public Polynomial differentiate(int power) {
        int[] diff = new int[coeffs.length - power < 0 ? 0 : coeffs.length - power];
        
        if (diff.length > 0) {
            System.arraycopy(coeffs, power, diff, 0, diff.length);
            for (int i = 0; i < diff.length; i++) {
                for (int j = i + 1; j <= i + power; j++) {
                    diff[i] *= j;
                }
            }
        }

        Polynomial res = new Polynomial(diff);
        res.trim();

        return res;
    }

    /**
     * Checks on equality with another polynomial.
     */
    public boolean equals(Polynomial other) {
        return Arrays.equals(coeffs, other.coeffs); 
    }

    /**
     * Convert polynomial to string.
     */
    public String toString() {
        String res = "";
        if (coeffs.length > 0) {
            for (int i = coeffs.length - 1; i > 0; i--) {
                if (i != 1) {
                    res += String.format("%dx^%d + ", coeffs[i], i);
                } else {
                    res += String.format("%dx + ", coeffs[i]);
                }
            }
            res += String.valueOf(coeffs[0]);
        }
        return res;
    }
}
