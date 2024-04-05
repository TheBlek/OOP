package ru.nsu.kuklin.snake;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.Objects;

/**
 * Handles whole game, has entry point.
 */
public class Game extends Application {
    /**
     * Entry point.
     */
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        stage.setTitle("Snake");
        toMainMenu(null);
    }

    /**
     * Create game scene with given field size and start game.
     */
    public void startGame(int fieldWidth, int fieldHeight) {
        var loader = new FXMLLoader(GameView.class.getResource("game.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            System.out.println("Failed to load game.fxml");
            return;
        }
        stage.setScene(scene);

        GameView view = loader.getController();

        var gameLoop = new Timeline();
        gameLoop.setCycleCount(Animation.INDEFINITE);

        var gameModel = new GameModel(fieldWidth, fieldHeight, 1);
        var processor = new GameProcessor(20);

        scene.setOnKeyPressed(processor.getKeyHandler());

        var kf = new KeyFrame(
            Duration.millis(16),
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (Objects.requireNonNull(processor.step(gameModel)) == StepStatus.LOSE) {
                        toMainMenu(
                            "You died at your own hands at length "
                                + gameModel.snake.size()
                                + "\nPlay again?");
                    }
                    view.draw(gameModel);
                }
            });

        gameLoop.getKeyFrames().add(kf);
        gameLoop.play();

        stage.show();
    }

    private void toMainMenu(String message) {
        var loader = new FXMLLoader(Game.class.getResource("main_menu.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            System.out.println("Failed to load game.fxml");
            return;
        }
        MainMenu menu = loader.getController();
        menu.setGame(this);
        if (message != null) {
            menu.sendMessage(message);
        }
        stage.setScene(scene);
        stage.show();
    }

    private Stage stage;
}