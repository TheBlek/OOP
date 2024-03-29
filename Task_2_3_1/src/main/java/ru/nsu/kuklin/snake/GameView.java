package ru.nsu.kuklin.snake;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameView {
    public GameView(GraphicsContext gc, int cellWidth, int cellHeight) {
        this.gc = gc;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }

    public void draw(GameModel game) {
        for (int i = 0; i < game.field.length; i++) {
            for (int j = 0; j < game.field[0].length; j++) {
                switch (game.field[i][j]) {
                    case FOOD:
                        gc.setFill(Color.RED);
                        gc.fillOval(i * cellWidth, j * cellHeight, cellWidth, cellHeight);
                        break;
                    case SNAKE:
                        gc.setFill(Color.GREEN);
                        gc.fillRect(i * cellWidth, j * cellHeight, cellWidth, cellHeight);
                        break;
                    case EMPTY:
                        gc.setFill(Color.WHITE);
                        gc.fillRect(i * cellWidth, j * cellHeight, cellWidth, cellHeight);
                        break;
                }
            }
        }
    }
    private GraphicsContext gc;
    private int cellWidth;
    private int cellHeight;
}
