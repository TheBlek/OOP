package ru.nsu.kuklin.pizzeria;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import ru.nsu.kuklin.pizzeria.queue.BlockingQueue;

/**
 * Tests for blocking queue.
 */
public class DequeTest {
    @Test
    public void testBlocking() {
        var deque = new BlockingQueue<Integer>(10);
        new Thread(() -> {
            try {
                System.out.println(deque.get());
            } catch (InterruptedException ignored) {}
        }).start();
        try {
            Thread.sleep(1000);
            deque.put(10);
        } catch (InterruptedException ignored) {}
    }

    public void testQueue() {
        var deque = new BlockingQueue<Integer>(10);
        try {
            deque.put(1);
            deque.put(2);
            deque.put(3);
            assertEquals(1, deque.get());
            assertEquals(2, deque.get());
            assertEquals(3, deque.get());
        } catch (InterruptedException ignored) {}
    }
}
