package ru.nsu.kuklin.snake;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;
import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * Testing snake model
 */
public class SnakeTest {
    @Test
    void simpleStep() {
        var model = new GameModel(5, 5, 0);
        var processor = new GameProcessor(1);
        assertEquals(processor.step(model), StepStatus.OK);
        var expectedSnake = new ArrayDeque<Point2D>();
        expectedSnake.add(new Point2D(0, 1));
        expectedSnake.add(new Point2D(0, 2));
        expectedSnake.add(new Point2D(0, 3));
        assertEquals(expectedSnake.size(), model.snake.size());
        Iterator<Point2D> expected = expectedSnake.iterator();
        Iterator<Point2D> actual = model.snake.iterator();

        while (expected.hasNext()) {
            assertEquals(expected.next(), actual.next());
        }
    }

    @Test
    void stubStep() {
        var model = new GameModel(5, 5, 0);
        var processor = new GameProcessor(20);
        assertEquals(processor.step(model), StepStatus.SKIP);
        var expectedSnake = new ArrayDeque<Point2D>();
        expectedSnake.add(new Point2D(0, 0));
        expectedSnake.add(new Point2D(0, 1));
        expectedSnake.add(new Point2D(0, 2));
        assertEquals(expectedSnake.size(), model.snake.size());
        Iterator<Point2D> expected = expectedSnake.iterator();
        Iterator<Point2D> actual = model.snake.iterator();

        while (expected.hasNext()) {
            assertEquals(expected.next(), actual.next());
        }
    }

    @Test
    void eatStep() {
        var model = new GameModel(1, 4, 1);
        var processor = new GameProcessor(1);
        assertEquals(processor.step(model), StepStatus.OK);
        var expectedSnake = new ArrayDeque<Point2D>();
        expectedSnake.add(new Point2D(0, 0));
        expectedSnake.add(new Point2D(0, 1));
        expectedSnake.add(new Point2D(0, 2));
        expectedSnake.add(new Point2D(0, 3));
        assertEquals(expectedSnake.size(), model.snake.size());
        Iterator<Point2D> expected = expectedSnake.iterator();
        Iterator<Point2D> actual = model.snake.iterator();

        while (expected.hasNext()) {
            assertEquals(expected.next(), actual.next());
        }
    }
}
