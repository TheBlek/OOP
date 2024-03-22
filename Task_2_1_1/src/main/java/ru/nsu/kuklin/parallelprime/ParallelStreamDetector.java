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
            .anyMatch(this::isComposite);
    }
}
