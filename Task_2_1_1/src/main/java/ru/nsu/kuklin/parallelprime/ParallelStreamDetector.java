package ru.nsu.kuklin.parallelprime;

import java.util.Arrays;

/**
 * Detects composite numbers using a parallel stream.
 */
public class ParallelStreamDetector implements CompositeNumberDetector {
    /**
     * Main method.
     */
    @Override
    public boolean detect(int[] nums) {
        return Arrays
            .stream(nums)
            .parallel()
            .map(num -> {
                for (int j = 2; j * j < num; j++) {
                    if (num % j == 0) {
                        return 0;
                    }
                }
                return 1;
            })
            .anyMatch(p -> p == 0);
    }
}
