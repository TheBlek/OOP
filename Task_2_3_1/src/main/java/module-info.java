module ru.nsu.kuklin.snake {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.nsu.kuklin.snake to javafx.fxml;
    exports ru.nsu.kuklin.snake;
}