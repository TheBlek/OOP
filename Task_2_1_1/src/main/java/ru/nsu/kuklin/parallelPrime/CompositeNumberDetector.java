package ru.nsu.kuklin.parallelPrime;

import java.util.Arrays;
import java.lang.ThreadGroup;
import java.lang.Thread;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.PrintStream;
import java.io.FileOutputStream;

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

    public static boolean detectParallelThreads(int[] nums, int thread_count) {
        ThreadGroup group = new ThreadGroup("Detectors");
        AtomicBoolean result = new AtomicBoolean(false);
        int next = 0;
        for (int i = 0; i < thread_count && next < nums.length; i++) {
            final int left = next;                               // Equally distribute surplas numbers
            final int right = left + nums.length / thread_count + (i < nums.length % thread_count ? 1 : 0);
            Thread t = new Thread(group, new Runnable() {
                public void run() {
                    for (int k = left; k < right; k++) {
                        for (int j = 2; j*j < nums[k]; j++) {
                            if (nums[k] % j == 0) {
                                result.set(true);
                                group.interrupt();
                                break;
                            }
                        }
                        if (Thread.currentThread().isInterrupted())
                            return;
                    }
                }
            });
            t.start();
            next = right;
        }
        
        Thread[] threads = new Thread[thread_count];
        int cnt = group.enumerate(threads);
        for (int i = 0; i < cnt; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
            }
        }

        return result.get();
    }
}


