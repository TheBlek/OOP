package ru.nsu.kuklin.parallelprime;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(false, (new SequentialDetector()).detect(new int[] {}));
    }

    @Test
    @DisplayName("sequential all primes")
    public void detectPrimes() {
        assertEquals(false, (new SequentialDetector()).detect(new int[] {1, 3, 7, 31}));
    }

    @Test
    @DisplayName("sequential last composite")
    public void detectLastComposite() {
        assertEquals(true, (new SequentialDetector()).detect(new int[] {1, 3, 7, 35}));
    }

    @Test
    @DisplayName("stream empty")
    public void detectEmptyStream() {
        assertEquals(false, (new ParallelStreamDetector()).detect(new int[] {}));
    }

    @Test
    @DisplayName("stream all primes")
    public void detectPrimesStream() {
        assertEquals(false, (new ParallelStreamDetector()).detect(new int[] {1, 3, 7, 31}));
    }

    @Test
    @DisplayName("stream last composite")
    public void detectLastCompositeStream() {
        assertEquals(true, (new ParallelStreamDetector()).detect(new int[] {1, 3, 7, 35}));
    }

    @Test
    @DisplayName("threads empty")
    public void detectEmptyThread() {
        assertEquals(false, (new RawThreadsDetector(4)).detect(new int[] {}));
    }

    @Test
    @DisplayName("threads all primes")
    public void detectPrimesThread() {
        assertEquals(
            false,
            (new RawThreadsDetector(4)).detect(new int[] {1, 3, 7, 31})
        );
    }

    /**
     * Ugh. This is not detected as a test and a doc is required
     */
    @ParameterizedTest
    @DisplayName("threads last composite")
    @ValueSource(ints = {1, 2, 4, 8, 16})
    public void detectLastCompositeThread(int threadCount) {
        assertEquals(
            true,
            (new RawThreadsDetector(threadCount)).detect(new int[] {1, 3, 7, 35})
        );
    }
}
