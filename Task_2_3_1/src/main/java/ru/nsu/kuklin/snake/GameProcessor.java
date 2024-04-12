package ru.nsu.kuklin.snake;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.Random;

/**
 * Processes game steps.
 */
public class GameProcessor {
    /**
     * Create a process with given step period.
     */
    public GameProcessor(int startPeriod) {
        this.startPeriod = startPeriod;
        this.period = this.startPeriod;
    }

    /**
     * Make one simulation step on the model.
     */
    public StepStatus step(GameModel data) {
        while (data.food.size() < data.targetFoodCount) {
            spawnFood(data);
        }
        step++;
        if (step % period != 0) {
            return StepStatus.SKIP;
        }
        data.snakeDirection = snakeDirection;
        lastSnakeStep = snakeDirection;
        var newPos = getNextHead(data);
        data.snake.add(newPos);
        switch (data.field[(int) newPos.getX()][(int) newPos.getY()]) {
            case EMPTY:
                var old = data.snake.remove();
                data.field[(int) old.getX()][(int) old.getY()] = CellType.EMPTY;
                break;
            case FOOD:
                var id = data.food.lastIndexOf(newPos);
                data.food.remove(id);
                untilNextSpeedUp -= 1;
                if (untilNextSpeedUp == 0) {
                    period -= 1;
                    untilNextSpeedUp = startPeriod - period + 1;
                }
                break;
            case SNAKE:
                return StepStatus.LOSE;
            default:
                break;
        }
        data.field[(int) newPos.getX()][(int) newPos.getY()] = CellType.SNAKE;
        return StepStatus.OK;
    }

    /**
     * Get key handler for events.
     */
    public EventHandler<KeyEvent> getKeyHandler() {
        return new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
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

    private void spawnFood(GameModel data) {
        int foodX;
        int foodY;
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
    private final int startPeriod;

    public int getPeriod() {
        return period;
    }

    private int period;
    private int untilNextSpeedUp = 1;
    private final Random rng = new Random();
}