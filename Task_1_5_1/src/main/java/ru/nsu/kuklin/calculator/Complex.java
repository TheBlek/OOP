package ru.nsu.kuklin.calculator;

import java.lang.Math;

/**
 * Record representing a complex number.
 */
public record Complex(double x, double y) {
    /**
     * Calculates sum of complex numbers.
     */
    public static Complex add(Complex lhs, Complex rhs) {
        return new Complex(lhs.x + rhs.x, lhs.y + rhs.y);
    }

    /**
     * Calculates difference of complex numbers.
     */
    public static Complex sub(Complex lhs, Complex rhs) {
        return new Complex(lhs.x - rhs.x, lhs.y - rhs.y);
    }

    /**
     * Calculates product of complex numbers.
     */
    public static Complex mul(Complex lhs, Complex rhs) {
        return new Complex(lhs.x * rhs.x - lhs.y * rhs.y, lhs.x * rhs.y + lhs.y * rhs.x);
    }

    /**
     * Calculates quotient of complex numbers.
     */
    public static Complex div(Complex lhs, Complex rhs) {
        double len = rhs.lenSqr();
        return new Complex(
            (lhs.x * rhs.x + lhs.y * rhs.y) / len,
            (-lhs.x * rhs.y + lhs.y * rhs.x) / len
        );
    }

    /**
     * Raises exponent to the power of complex number.
     */
    static Complex exp(Complex z) {
        return new Complex(Math.exp(z.x) * Math.cos(z.y), Math.exp(z.x) * Math.sin(z.y));
    }

    /**
     * Takes logarithm of a complex number. Returns main branch.
     */
    static Complex log(Complex z) {
        double arg = Math.atan(z.y / z.x);
        if (z.x < 0) {
            arg += Math.PI;
        }
        return new Complex(Math.log(Math.sqrt(z.lenSqr())), arg);
    }

    /**
     * Takes cosine of a complex number.
     */
    static Complex cos(Complex z) {
        return new Complex(
            Math.cos(z.x) * (Math.exp(-z.y) + Math.exp(z.y)) / 2,
            Math.sin(z.x) * (Math.exp(-z.y) - Math.exp(z.y)) / 2);
    }

    /**
     * Takes sine of a complex number.
     */
    static Complex sin(Complex z) {
        return new Complex(
            Math.sin(z.x) * (Math.exp(-z.y) + Math.exp(z.y)) / 2,
            -Math.cos(z.x) * (Math.exp(-z.y) - Math.exp(z.y)) / 2);
    }

    /**
     * Takes square root of a complex number. Main branch.
     */
    static Complex sqrt(Complex z) {
        double len = Math.sqrt(Math.sqrt(z.lenSqr()));
        double arg = Math.atan(z.y / z.x);
        if (z.x < 0) {
            arg += Math.PI;
        }
        Complex radius = Complex.exp(new Complex(0, arg/2));
        return new Complex(len * radius.x, len * radius.y);
    }

    /**
     * Calculates length squared.
     */
    public double lenSqr() {
        return x * x + y * y;
    }
}
