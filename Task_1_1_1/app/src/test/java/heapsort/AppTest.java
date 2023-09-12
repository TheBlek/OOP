
package ru.nsu.kuklin.heapsort;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Class for tests
 */
public class AppTest {
    @Test
    @DisplayName("empty")
    public void heapsortTestEmpty() {
        int[] res = App.heapsort(new int[]{});
        assertTrue(Arrays.equals(res, new int[]{}));
    }

    @Test
    @DisplayName("sorted")
    public void heapsortTestSorted() {
        int[] res = App.heapsort(new int[]{1, 2, 3, 4, 5, 6});
        assertTrue(Arrays.equals(res, new int[]{1, 2, 3, 4, 5, 6}));
    }

    @Test
    @DisplayName("reverse-sorted")
    public void heapsortTestReverseSorted() {
        int[] res = App.heapsort(new int[]{6, 5, 4, 3, 2, 1});
        assertTrue(Arrays.equals(res, new int[]{1, 2, 3, 4, 5, 6}));
    }

    @Test
    @DisplayName("sorted-shift")
    public void heapsortTestSortedShift() {
        int[] res = App.heapsort(new int[]{6, 1, 2, 3, 4, 5});
        assertTrue(Arrays.equals(res, new int[]{1, 2, 3, 4, 5, 6}));
    }
}
