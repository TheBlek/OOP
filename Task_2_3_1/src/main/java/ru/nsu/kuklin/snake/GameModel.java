package ru.nsu.kuklin.snake;

import javafx.geometry.Point2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

/**
 * Snake game model. Contains only data and initialization methods.
 */
public class GameModel {
    /**
     * Create Snake game with given parameters.
     */
    public GameModel(int fieldWidth, int fieldHeight, int foodCount) {
        field = new CellType[fieldWidth][fieldHeight];
        snake = new ArrayDeque<>();
        targetFoodCount = foodCount;
        food = new ArrayList<>(targetFoodCount);
        this.fieldHeight = fieldHeight;
        this.fieldWidth = fieldWidth;
        startUp();
    }

    /**
     * Setup new snake game. Can be used to restart game to reuse GameModel object.
     */
    public void startUp() {
        for (int i = 0; i < fieldWidth; i++) {
            for (int j = 0; j < fieldHeight; j++) {
                field[i][j] = CellType.EMPTY;
            }
        }
        snake.clear();
        snake.add(new Point2D(0, 0));
        snake.add(new Point2D(0, 1));
        snake.add(new Point2D(0, 2));
        for (var seg : snake) {
            field[(int) seg.getX()][(int) seg.getY()] = CellType.SNAKE;
        }
        snakeDirection = Direction.DOWN;
        food.clear();
    }

    public Deque<Point2D> snake;
    public Direction snakeDirection;
    public CellType[][] field;
    public int fieldHeight;
    public int fieldWidth;
    public ArrayList<Point2D> food;
    public int targetFoodCount;
}
