package ru.nsu.kuklin.parallelPrime;

import java.util.Random;
import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
public class OnlyLastCompositeThreadCount {
    @Param({ "1000", "10000", "100000", "1000000" })
    public int size;
    public int[] nums;
    @Param({"1", "2", "4", "8", "16"})
    public int threadCount;

    @Setup(Level.Invocation)
    public void setUp() {
        Random rng = new Random();
        nums = new int[size];
        for (int i = 0; i < size-1; i++)
            nums[i] = 1000000007;
        nums[size-1] = rng.nextInt() * 101;
    }
}
