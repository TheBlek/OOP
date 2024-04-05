package ru.nsu.kuklin.snake;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

/**
 * Main menu class that handles interaction in the main menu.
 */
public class MainMenu {

    /**
     * Play button callback.
     */
    public void startGame(ActionEvent event) {
        game.startGame(fieldWidth, fieldHeight);
    }

    /**
     * Set attached Game.
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Width change event callback.
     */
    public void widthChanged() {
        this.fieldWidth = width.getValue();
    }

    /**
     * Height change event callback.
     */
    public void heightChanged() {
        this.fieldHeight = height.getValue();
    }

    /**
     * Set message text.
     */
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
