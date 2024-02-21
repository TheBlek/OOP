package ru.nsu.kuklin.parallelprime;

import java.util.Random;
import org.openjdk.jmh.annotations.*;

/*
 * Class for testing composite detector on big array.
 * Array consist of primes where only the last is composite.
 */
@State(Scope.Benchmark)
public class OnlyLastComposite {
    @Param({ "1000", "10000", "100000", "1000000" })
    public int size;
    public int[] nums;

    /*
     * Setup function.
     */
    @Setup(Level.Invocation)
    public void setUp() {
        Random rng = new Random();
        nums = new int[size];
        for (int i = 0; i < size-1; i++) {
            nums[i] = 1000000007;
        }
        nums[size-1] = rng.nextInt() * 101;
    }
}
