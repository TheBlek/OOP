package ru.nsu.kuklin.snake;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class GameView {
    public void draw(GameModel game) {
        if (gc == null) {
            gc = canvas.getGraphicsContext2D();
        }
        int cellHeight = (int) (canvas.getHeight() / game.fieldHeight);
        int cellWidth = (int) (canvas.getWidth() / game.fieldWidth);
        score.setText("Score: " + game.snake.size());
        for (int i = 0; i < game.field.length; i++) {
            for (int j = 0; j < game.field[0].length; j++) {
                switch (game.field[i][j]) {
                    case FOOD:
                        gc.setFill(Color.WHITE);
                        gc.fillRect(i * cellWidth, j * cellHeight, cellWidth, cellHeight);
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
    @FXML
    private ResizableCanvas canvas;
    @FXML
    private Label score;
    @FXML
    private Pane pane;
    private GraphicsContext gc = null;
}
