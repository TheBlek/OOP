package ru.nsu.kuklin.snake;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

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
    public void sendMessage(String text) {
        message.setText(text);
    }

    private int fieldWidth = 99;
    private int fieldHeight = 99;
    @FXML
    private ChoiceBox<Integer> width;
    @FXML
    private ChoiceBox<Integer> height;
    @FXML
    private Label message;
    private Game game;
}
