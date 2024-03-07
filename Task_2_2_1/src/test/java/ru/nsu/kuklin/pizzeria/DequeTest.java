package ru.nsu.kuklin.pizzeria;

import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;

public class DequeTest {
    @Test
    public void testBlocking() {
        var deque = new BlockingDeque<Integer>(10);
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
}
