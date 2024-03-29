package ru.nsu.kuklin.snake;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Cell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Random;

public class GameProcessor {
    public GameProcessor() {
    }

    public void step(GameModel data) {
        while (data.food.size() < data.targetFoodCount) {
            spawnFood(data);
        }
        step++;
        if (step % period != 0) {
            return;
        }
        data.snakeDirection = snakeDirection;
        lastSnakeStep = snakeDirection;
        var newPos = getNextHead(data);
        data.snake.add(newPos);
        switch (data.field[(int)newPos.getX()][(int)newPos.getY()]) {
            case EMPTY:
                var old = data.snake.remove();
                data.field[(int)old.getX()][(int)old.getY()] = CellType.EMPTY;
                break;
            case FOOD:
                var id = data.food.lastIndexOf(newPos);
                data.food.remove(id);
                break;
            case SNAKE:
                data.startUp();
                snakeDirection = Direction.DOWN;
                lastSnakeStep = Direction.DOWN;
                return;
        }
        data.field[(int)newPos.getX()][(int)newPos.getY()] = CellType.SNAKE;
    }

    private static Point2D getNextHead(GameModel data) {
        var head = data.snake.getLast();
        var newPos = switch (data.snakeDirection) {
            case UP -> {
                if (head.getY() != 0) {
                    yield new Point2D(head.getX(), (head.getY() - 1) % data.field[0].length);
                } else {
                    yield new Point2D(head.getX(), data.field[0].length - 1);
                }
            }
            case DOWN -> new Point2D(head.getX(), (head.getY() + 1) % data.field[0].length);
            case LEFT -> {
                if (head.getX() != 0) {
                    yield new Point2D((head.getX() - 1) % data.field.length, head.getY());
                } else {
                    yield new Point2D(data.field.length - 1, head.getY());
                }
            }
            case RIGHT -> new Point2D((head.getX() + 1) % data.field.length, head.getY());
        };
        return newPos;
    }

    public EventHandler<KeyEvent> getKeyHandler() {
        return new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println(event.getCode());
                if (event.getCode().equals(KeyCode.A) && lastSnakeStep != Direction.RIGHT) {
                    snakeDirection = Direction.LEFT;
                } else if (event.getCode().equals(KeyCode.D) && lastSnakeStep != Direction.LEFT) {
                    snakeDirection = Direction.RIGHT;
                } else if (event.getCode().equals(KeyCode.W) && lastSnakeStep != Direction.DOWN) {
                    snakeDirection = Direction.UP;
                } else if (event.getCode().equals(KeyCode.S) && lastSnakeStep != Direction.UP) {
                    snakeDirection = Direction.DOWN;
                }
            }
        };
    }
    private void spawnFood(GameModel data) {
        int foodX = 0;
        int foodY = 0;
        do {
            foodX = rng.nextInt(0, data.field.length);
            foodY = rng.nextInt(0, data.field[0].length);
        } while (data.field[foodX][foodY] != CellType.EMPTY);
        data.field[foodX][foodY] = CellType.FOOD;
        data.food.add(new Point2D(foodX, foodY));
    }
    private Direction snakeDirection = Direction.DOWN;
    private Direction lastSnakeStep = Direction.DOWN;
    private int step = 0;
    private int period = 20;
    private final Random rng = new Random();
}
