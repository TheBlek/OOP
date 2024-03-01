package ru.nsu.kuklin.parallelprime;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Detects composite numbers using raw threads.
 */
public class RawThreadsDetector implements CompositeNumberDetector {
    /**
     * Construct to use a certain amount of threads.
     */
    public RawThreadsDetector(int threadCount) {
        this.threadCount = threadCount;
    }

    /**
     * Main method.
     */
    @Override
    public boolean detect(int[] nums) {
        ThreadGroup group = new ThreadGroup("Detectors");
        AtomicBoolean result = new AtomicBoolean(false);
        int next = 0;
        for (int i = 0; i < threadCount && next < nums.length; i++) {
            int l = next;                               // Equally distribute surplas numbers
            int r = l + nums.length / threadCount + (i < nums.length % threadCount ? 1 : 0);
            Thread t = new Thread(group, new Runnable() {
                public void run() {
                    for (int k = l; k < r; k++) {
                        for (int j = 2; j * j < nums[k]; j++) {
                            if (nums[k] % j == 0) {
                                result.set(true);
                                group.interrupt();
                                break;
                            }
                        }
                        if (Thread.currentThread().isInterrupted()) {
                            return;
                        }
                    }
                }
            });
            t.start();
            next = r;
        }

        Thread[] threads = new Thread[threadCount];
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
    private final int threadCount;
}
