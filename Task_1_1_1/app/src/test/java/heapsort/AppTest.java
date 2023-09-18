
package ru.nsu.kuklin.heapsort;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
        int[] res = new int[]{};
        App.heapsort(res);
        assertTrue(Arrays.equals(res, new int[]{}));
    }

    @Test
    @DisplayName("one element")
    public void heapsortTestOne() {
        int[] res = new int[]{-100};
        App.heapsort(res);
        assertTrue(Arrays.equals(res, new int[]{-100}));
    }

    @Test
    @DisplayName("sorted")
    public void heapsortTestSorted() {
        int[] res = new int[]{1, 2, 3, 4, 5, 6};
        App.heapsort(res);
        assertTrue(Arrays.equals(res, new int[]{1, 2, 3, 4, 5, 6}));
    }

    @Test
    @DisplayName("reverse-sorted")
    public void heapsortTestReverseSorted() {
        int[] res = new int[]{6, 5, 4, 3, 2, 1};
        App.heapsort(res);
        assertTrue(Arrays.equals(res, new int[]{1, 2, 3, 4, 5, 6}));
    }

    @Test
    @DisplayName("sorted-shift")
    public void heapsortTestSortedShift() {
        int[] res = new int[]{6, -1, 2, 3, 4, 5};
        App.heapsort(res);
        assertTrue(Arrays.equals(res, new int[]{-1, 2, 3, 4, 5, 6}));
    }

    @Test
    @DisplayName("big-ass")
    public void heapsortTestBig() {
        int size = 1000000;
        int[] input = new int[size];
        Random rng = new Random();
        for (int i = 0; i < size; i++) {
            input[i] = rng.nextInt() % 10000;
        }
        int[] mine = input.clone();
        App.heapsort(mine);
        Arrays.sort(input);
        assertTrue(Arrays.equals(mine, input));
    }
}
