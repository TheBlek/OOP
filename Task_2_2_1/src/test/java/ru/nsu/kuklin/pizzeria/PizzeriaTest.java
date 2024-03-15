package ru.nsu.kuklin.pizzeria;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PizzeriaTest {
    @Test
    public void interruptTest() throws InterruptedException, AWTException {
        Pizzeria p = new Pizzeria(
                "configs/defaultStorage.json",
                "configs/bakers1.json",
                "configs/deliverers1.json",
                "configs/customers1.json");
        var t = new Thread(() -> p.run(new ExitCondition() {
            @Override
            public void waitForExitCondition() {
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        }));
        t.start();
        t.join();
        assertEquals(0, p.getState().getOrders().getSize());
    }
}
