package ru.nsu.kuklin.snake;

import javafx.geometry.Point2D;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class GameModel {
    public GameModel(int fieldWidth, int fieldHeight, int foodCount) {
        field = new CellType[fieldWidth][fieldHeight];
        snake = new ArrayDeque<>();
        targetFoodCount = foodCount;
        food = new ArrayList<>(targetFoodCount);
        startUp();
    }
    public void startUp() {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                field[i][j] = CellType.EMPTY;
            }
        }
        snake.clear();
        snake.add(new Point2D(0, 0));
        snake.add(new Point2D(0, 1));
        snake.add(new Point2D(0, 2));
        for (var seg : snake)
            field[(int)seg.getX()][(int)seg.getY()] = CellType.SNAKE;
        snakeDirection = Direction.DOWN;
        food.clear();
    }
    public Deque<Point2D> snake;
    public Direction snakeDirection;
    public Direction lastSnakeStep;
    public CellType[][] field;
    public ArrayList<Point2D> food;
    public int targetFoodCount;
    public boolean isGameOver = false;
    public boolean isGameWin = false;
}
