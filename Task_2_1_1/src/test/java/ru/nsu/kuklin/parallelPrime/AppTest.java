package ru.nsu.kuklin.parallelPrime;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

/**
 * Class for tests.
 */
public class AppTest {
    @Test
    @DisplayName("sequential empty")
    public void detectEmpty() {
        assertEquals(false, CompositeNumberDetector.detectSequential(new int[] {}));
    }

    @Test
    @DisplayName("sequential all primes")
    public void detectPrimes() {
        assertEquals(false, CompositeNumberDetector.detectSequential(new int[] {1, 3, 7, 31}));
    }

    @Test
    @DisplayName("sequential last composite")
    public void detectLastComposite() {
        assertEquals(true, CompositeNumberDetector.detectSequential(new int[] {1, 3, 7, 35}));
    }

    @Test
    @DisplayName("stream empty")
    public void detectEmptyStream() {
        assertEquals(false, CompositeNumberDetector.detectParallelStream(new int[] {}));
    }

    @Test
    @DisplayName("stream all primes")
    public void detectPrimesStream() {
        assertEquals(false, CompositeNumberDetector.detectParallelStream(new int[] {1, 3, 7, 31}));
    }

    @Test
    @DisplayName("stream last composite")
    public void detectLastCompositeStream() {
        assertEquals(true, CompositeNumberDetector.detectParallelStream(new int[] {1, 3, 7, 35}));
    }

    @Test
    @DisplayName("threads empty")
    public void detectEmptyThread() {
        assertEquals(false, CompositeNumberDetector.detectParallelThreads(new int[] {}, 4));
    }

    @Test
    @DisplayName("threads all primes")
    public void detectPrimesThread() {
        assertEquals(
            false,
            CompositeNumberDetector.detectParallelThreads(new int[] {1, 3, 7, 31}, 4)
        );
    }

    @ParameterizedTest
    @DisplayName("threads last composite")
    @ValueSource(ints = {1, 2, 4, 8, 16})
    public void detectLastCompositeThread(int threadCount) {
        assertEquals(
            true,
            CompositeNumberDetector.detectParallelThreads(new int[] {1, 3, 7, 35}, threadCount)
        );
    }
}
