package ru.nsu.kuklin.pizzeria.queue;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayDeque;
import java.util.concurrent.Semaphore;

/**
 * Blocking queue with fixed capacity implementation.
 */
public class BlockingQueue<T> implements IBlockingQueue<T> {
    /**
     * Construct queue with given capacity.
     */
    public BlockingQueue(int numElements) {
        deque = new ArrayDeque<T>(numElements);
        occupiedSpace = new Semaphore(0);
        freeSpace = new Semaphore(numElements);
    }

    /**
     * Put element at the end of the queue blocking until there is enough space.
     */
    public void put(@NotNull T o) throws InterruptedException {
        freeSpace.acquire();
        synchronized (deque) {
            deque.add(o);
        }
        occupiedSpace.release();
    }

    /**
     * Get first element of the queue blocking until there is one.
     */
    @NotNull
    public T get() throws InterruptedException {
        occupiedSpace.acquire();
        T res;
        synchronized (deque) {
            res = deque.poll();
        }
        freeSpace.release();
        return res;
    }

    /**
     * Get current queue length.
     */
    public int getSize() {
        synchronized (deque) {
            return deque.size();
        }
    }

    private final ArrayDeque<T> deque;
    private final Semaphore occupiedSpace;
    private final Semaphore freeSpace;
}
