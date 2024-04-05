package ru.nsu.kuklin.snake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class SnakeTest {
    @Test
    void simpleStep() {
        var model = new GameModel(5, 5, 0);
        var processor = new GameProcessor(1);
        processor.step(model);
        var expectedSnake = new ArrayDeque<Point2D>();
        expectedSnake.add(new Point2D(0, 1));
        expectedSnake.add(new Point2D(0, 2));
        expectedSnake.add(new Point2D(0, 3));
        assertEquals(3, model.snake.size());
        Iterator<Point2D> sIterator = expectedSnake.iterator();
        Iterator<Point2D> tIterator = model.snake.iterator();

        while (sIterator.hasNext()) {
            assertEquals(sIterator.next(), tIterator.next());
        }
    }

    @Test
    void stubStep() {
        var model = new GameModel(5, 5, 0);
        var processor = new GameProcessor(20);
        processor.step(model);
        var expectedSnake = new ArrayDeque<Point2D>();
        expectedSnake.add(new Point2D(0, 0));
        expectedSnake.add(new Point2D(0, 1));
        expectedSnake.add(new Point2D(0, 2));
        assertEquals(3, model.snake.size());
        Iterator<Point2D> sIterator = expectedSnake.iterator();
        Iterator<Point2D> tIterator = model.snake.iterator();

        while (sIterator.hasNext()) {
            assertEquals(sIterator.next(), tIterator.next());
        }
    }
}
