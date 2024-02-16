package ru.nsu.kuklin.parallelPrime;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}
