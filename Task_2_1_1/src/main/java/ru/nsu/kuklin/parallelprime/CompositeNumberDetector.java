package ru.nsu.kuklin.parallelprime;

/**
 * Interface for Detectors of composite numbers.
 */
public interface CompositeNumberDetector {
    /**
     * Main method.
     * @param nums To search a composite number in
     * @return Whether there are composite numbers inside
     */
    boolean detect(int[] nums);
}
