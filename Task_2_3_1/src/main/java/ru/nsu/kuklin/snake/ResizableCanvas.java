package ru.nsu.kuklin.snake;

import javafx.scene.canvas.Canvas;

/**
 * Canvas that is able to resize on its own.
 */
public class ResizableCanvas extends Canvas {

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double maxHeight(double width) {
        return Double.MAX_VALUE;
    }

    @Override
    public double maxWidth(double height) {
        return Double.MAX_VALUE;
    }

    @Override
    public double minWidth(double height) {
        return 10D;
    }

    @Override
    public double minHeight(double width) {
        return 10D;
    }

    @Override
    public void resize(double width, double height) {
        this.setWidth(width);
        this.setHeight(height);
    }
}