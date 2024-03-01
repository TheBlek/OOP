package ru.nsu.kuklin.parallelprime;

/**
 * Detects composite numbers sequentially
 */
public class SequentialDetector implements CompositeNumberDetector {

    /**
     * Main method.
     */
    @Override
    public boolean detect(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            i++;
            boolean prime = true;
            for (int j = 2; j * j < nums[i]; j++) {
                if (nums[i] % j == 0) {
                    prime = false;
                    break;
                }
            }
            if (!prime) {
                return true;
            }
        }
        return false;
    }
}
