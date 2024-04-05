package ru.nsu.kuklin.snake;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class MainMenu {

    public void startGame(ActionEvent event) {
        game.startGame(fieldWidth, fieldHeight);
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void widthChanged() {
        this.fieldWidth = width.getValue();
    }

    public void heightChanged() {
        this.fieldHeight = height.getValue();
    }

    private int fieldWidth = 99;
    private int fieldHeight = 99;
    @FXML
    private ChoiceBox<Integer> width;
    @FXML
    private ChoiceBox<Integer> height;
    private Game game;
}
