package ru.nsu.kuklin.snake;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class Game extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        var loader = new FXMLLoader(GameView.class.getResource("game.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);

        GameView view = loader.getController();

        var gameLoop = new Timeline();
        gameLoop.setCycleCount(Animation.INDEFINITE);

        var fieldWidth = 27;
        var fieldHeight = 18;

        var gameModel = new GameModel(fieldWidth, fieldHeight, 1);
        var processor = new GameProcessor();

        scene.setOnKeyPressed(processor.getKeyHandler());

        var kf = new KeyFrame(
            Duration.millis(16),
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    processor.step(gameModel);
                    view.draw(gameModel);
                }
            });

        gameLoop.getKeyFrames().add(kf);
        gameLoop.play();

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}