package ru.nsu.kuklin.parallelprime;

import java.util.Random;
import org.openjdk.jmh.annotations.*;

/*
 * Class for testing composite detector threaded on big array of random numbers
 */
@State(Scope.Benchmark)
public class FullyRandomThreadCount {
    @Param({ "1000000" })
    public int size;
    public int[] nums;
    @Param({"1", "2", "4", "8", "16"})
    public int threadCount;

    @Setup(Level.Invocation)
    public void setUp() {
        Random rng = new Random();
        nums = new int[size];
        for (int i = 0; i < size; i++) {
            nums[i] = rng.nextInt();
        }
    }
}
