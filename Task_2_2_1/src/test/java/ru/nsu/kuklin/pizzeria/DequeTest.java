package ru.nsu.kuklin.pizzeria;

import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;

public class DequeTest {
    @Test
    public void testBlocking() {
        var deque = new BlockingDeque<Integer>(10);
        new Thread() {
            public void run() {
                System.out.println(deque.pop());
            }
        }.start();
        try {
            Thread.sleep(1000);
        } catch (Exception ignored) {}
        deque.push(10);
    }
}
