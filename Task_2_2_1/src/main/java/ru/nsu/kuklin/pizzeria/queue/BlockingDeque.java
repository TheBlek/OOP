package ru.nsu.kuklin.pizzeria.queue;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.concurrent.Semaphore;

public class BlockingDeque<T> implements IBlockingQueue<T> {
    public BlockingDeque(int numElements) {
        deque = new ArrayDeque<T>(numElements);
        size = numElements;
        occupiedSpace = new Semaphore(0);
        freeSpace = new Semaphore(numElements);
    }

    public void put(@NotNull T o) throws InterruptedException {
        freeSpace.acquire();
        occupiedSpace.release();
        synchronized(deque) {
            deque.add(o);
        }
    }

    @NotNull
    public T get() throws InterruptedException {
        occupiedSpace.acquire();
        freeSpace.release();
        T res;
        synchronized(deque) {
            res = deque.poll();
        }
        return res;
    }

    private final ArrayDeque<T> deque;
    private final int size;
    private final Semaphore occupiedSpace;
    private final Semaphore freeSpace;
}
