package ru.nsu.kuklin.parallelprime;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Benchmarks for composite detector.
 */
@State(Scope.Benchmark)
public class Benchmarks {
    /**
     * Bench.
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void sequentialRandom(Blackhole bh, FullyRandomDetectorState state) throws Exception {
        bh.consume((new SequentialDetector()).detect(state.nums));
    }

    /**
     * Bench.
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void sequentialLast(Blackhole bh, OnlyLastComposite state) throws Exception {
        bh.consume((new SequentialDetector()).detect(state.nums));
    }

    /**
     * Bench.
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void streamRandom(Blackhole bh, FullyRandomDetectorState state) throws Exception {
        bh.consume((new ParallelStreamDetector()).detect(state.nums));
    }
    
    /**
     * Bench.
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void streamLast(Blackhole bh, OnlyLastComposite state) throws Exception {
        bh.consume((new ParallelStreamDetector()).detect(state.nums));
    }

    /**
     * Bench.
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void threadRandom(Blackhole bh, FullyRandomThreadCount state) throws Exception {
        bh.consume(
                (new RawThreadsDetector(state.threadCount)).detect(state.nums)
        );
    }

    /**
     * Bench.
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void threadLast(Blackhole bh, OnlyLastCompositeThreadCount state) throws Exception {
        bh.consume(
                (new RawThreadsDetector(state.threadCount)).detect(state.nums)
        );
    }
}
