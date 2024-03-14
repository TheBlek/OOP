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
        synchronized(deque) {
            deque.add(o);
        }
        occupiedSpace.release();
    }

    @NotNull
    public T get() throws InterruptedException {
        occupiedSpace.acquire();
        T res;
        synchronized(deque) {
            res = deque.poll();
        }
        freeSpace.release();
        return res;
    }

    private final ArrayDeque<T> deque;
    private final int size;
    private final Semaphore occupiedSpace;
    private final Semaphore freeSpace;
}
