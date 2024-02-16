package ru.nsu.kuklin.parallelPrime;

import java.util.Arrays;

public class CompositeNumberDetector {
    public static boolean detectSequential(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            i++;
            boolean prime = true;
            for (int j = 2; j*j < nums[i]; j++) {
                if (nums[i] % j == 0) {
                    prime = false;
                    break;
                }
            }
            if (!prime)
                return true;
        }
        return false;
    }

    public static boolean detectParallelStream(int[] nums) {
        return Arrays
            .stream(nums)
            .parallel()
            .map(num -> {
                for (int j = 2; j*j < num; j++)
                    if (num % j == 0)
                        return 0;
                return 1;
            })
            .anyMatch(p -> p == 0);
    }
}


