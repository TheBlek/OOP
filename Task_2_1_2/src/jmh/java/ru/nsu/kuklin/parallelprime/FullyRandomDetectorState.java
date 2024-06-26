package ru.nsu.kuklin.parallelprime;

import java.util.Random;
import org.openjdk.jmh.annotations.*;

/**
 * Class for testing composite detector on big array of random numbers.
 */
@State(Scope.Benchmark)
public class FullyRandomDetectorState {
    @Param({ "1000000" })
    public int size;
    public int[] nums;

    /**
     * Setup function.
     */
    @Setup(Level.Invocation)
    public void setUp() {
        Random rng = new Random();
        nums = new int[size];
        for (int i = 0; i < size; i++) {
            nums[i] = rng.nextInt();
        }
    }
}
