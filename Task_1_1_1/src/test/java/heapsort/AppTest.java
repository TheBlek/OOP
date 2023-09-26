
package ru.nsu.kuklin.heapsort;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Class for tests.
 */
public class AppTest {
    @Test
    @DisplayName("empty")
    public void heapsortTestEmpty() {
        int[] actual = new int[]{};
        App.heapsort(actual);
        assertArrayEquals(actual, new int[]{});
    }

    @Test
    @DisplayName("one element")
    public void heapsortTestOne() {
        int[] actual = new int[]{-100};
        App.heapsort(actual);
        assertArrayEquals(actual, new int[]{-100});
    }

    @Test
    @DisplayName("sorted")
    public void heapsortTestSorted() {
        int[] actual = new int[]{1, 2, 3, 4, 5, 6};
        App.heapsort(actual);
        assertArrayEquals(actual, new int[]{1, 2, 3, 4, 5, 6});
    }

    @Test
    @DisplayName("reverse-sorted")
    public void heapsortTestReverseSorted() {
        int[] actual = new int[]{6, 5, 4, 3, 2, 1};
        App.heapsort(actual);
        assertArrayEquals(actual, new int[]{1, 2, 3, 4, 5, 6});
    }

    @Test
    @DisplayName("sorted-shift")
    public void heapsortTestSortedShift() {
        int[] actual = new int[]{6, -1, 2, 3, 4, 5};
        App.heapsort(actual);
        assertArrayEquals(actual, new int[]{-1, 2, 3, 4, 5, 6});
    }

    @Test
    @DisplayName("big-ass")
    public void heapsortTestBig() {
        int size = 1000000;
        int[] expected = new int[size];
        Random rng = new Random();
        for (int i = 0; i < size; i++) {
            expected[i] = rng.nextInt() % 10000;
        }
        int[] actual = expected.clone();
        App.heapsort(actual);
        Arrays.sort(expected);
        assertArrayEquals(actual, expected);
    }
}
