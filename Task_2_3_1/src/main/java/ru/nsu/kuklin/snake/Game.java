package ru.nsu.kuklin.snake;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class Game extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Game.class.getResource("hello-view.fxml"));
        Group group = new Group();
        Scene scene = new Scene(group);
        stage.setTitle("Hello!");
        stage.setScene(scene);

        var canvas = new Canvas(900, 600);
        group.getChildren().add(canvas);
        var gc = canvas.getGraphicsContext2D();

        var gameLoop = new Timeline();
        gameLoop.setCycleCount(Animation.INDEFINITE);

        var fieldWidth = 27;
        var fieldHeight = 18;
        var cellWidth = canvas.getWidth() / fieldWidth;
        var cellHeight = canvas.getHeight() / fieldHeight;

        var gameModel = new GameModel(fieldWidth, fieldHeight, 1);
        var processor = new GameProcessor();
        var view = new GameView(gc, (int)cellWidth, (int)cellHeight);

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