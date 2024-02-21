package ru.nsu.kuklin.parallelprime;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class Benchmarks {
    /*
     * Bench
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void sequentialRandom(Blackhole bh, FullyRandomDetectorState state) throws Exception {
        bh.consume(CompositeNumberDetector.detectSequential(state.nums));
    }

    /*
     * Bench
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void sequentialLast(Blackhole bh, OnlyLastComposite state) throws Exception {
        bh.consume(CompositeNumberDetector.detectSequential(state.nums));
    }

    /*
     * Bench
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void streamRandom(Blackhole bh, FullyRandomDetectorState state) throws Exception {
        bh.consume(CompositeNumberDetector.detectParallelStream(state.nums));
    }
    
    /*
     * Bench
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void streamLast(Blackhole bh, OnlyLastComposite state) throws Exception {
        bh.consume(CompositeNumberDetector.detectParallelStream(state.nums));
    }

    /*
     * Bench
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void threadRandom(Blackhole bh, FullyRandomThreadCount state) throws Exception {
        bh.consume(
            CompositeNumberDetector.detectParallelThreads(state.nums, state.threadCount)
        );
    }

    /*
     * Bench
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void threadLast(Blackhole bh, OnlyLastCompositeThreadCount state) throws Exception {
        bh.consume(
            CompositeNumberDetector.detectParallelThreads(state.nums, state.threadCount)
        );
    }
}
