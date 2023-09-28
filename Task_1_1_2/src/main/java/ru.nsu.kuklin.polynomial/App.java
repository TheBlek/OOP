
package ru.nsu.kuklin.polynomial;

/**
 * Stub description. FUN.
 */
public class App {
    /**
     * Stub description. FUN.
     *
     * @param args Stub. FUN.
     */
    public static void main(String[] args) {
        Polynomial p = new Polynomial(new int[] {1290, 1, 2, 5, 3});
        System.out.println(p.differentiate(15).toString());
    }
}
