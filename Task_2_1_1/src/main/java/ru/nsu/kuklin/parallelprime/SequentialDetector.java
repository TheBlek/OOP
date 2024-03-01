package ru.nsu.kuklin.parallelprime;

/**
 * Detects composite numbers sequentially.
 */
public class SequentialDetector implements CompositeNumberDetector {

    /**
     * Main method.
     */
    @Override
    public boolean detect(int[] nums) {
        for (int num : nums) {
            if (isComposite(num)) {
                return true;
            }
        }
        return false;
    }
}
