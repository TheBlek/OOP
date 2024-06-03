package ru.nsu.kuklin.parallelprime;

/**
 * Interface for Detectors of composite numbers.
 */
public interface CompositeNumberDetector {
    /**
     * Main method.
     */
    boolean detect(int[] nums);

    /**
     * Checks if number is composite.
     */
    default boolean isComposite(int num) {
        for (int j = 2; j * j < num; j++) {
            if (num % j == 0) {
                return true;
            }
        }
        return false;
    }
}
