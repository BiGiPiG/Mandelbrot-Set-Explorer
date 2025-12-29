package io.github.bigpig.mandelbrot_set_explorer.handlers;

import io.github.bigpig.mandelbrot_set_explorer.utils.Point;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SelectionHandler {
    private final Pane pane;
    private Rectangle selection;
    private boolean isCreatedSelectionRectangle = false;

    private static final double LEFT_BOARD = 10;
    private static final double RIGHT_BOARD = 510;
    private static final double TOP_BOARD = 10;
    private static final double BOTTOM_BOARD = 510;

    private double initialX;
    private double initialY;

    private Point topLeftCorner;
    private Point bottomRightCorner;

    public SelectionHandler(Pane pane) {
        this.pane = pane;
    }

    public void setInitialPoint(double x, double y) {
        this.initialX = clamp(x, LEFT_BOARD, RIGHT_BOARD);
        this.initialY = clamp(y, TOP_BOARD, BOTTOM_BOARD);
        this.isCreatedSelectionRectangle = false;
    }

    public void makeSelection(double currentX, double currentY) {
        double clampedCurrentX = clamp(currentX, LEFT_BOARD, RIGHT_BOARD);
        double clampedCurrentY = clamp(currentY, TOP_BOARD, BOTTOM_BOARD);

        double width = Math.abs(clampedCurrentX - initialX);
        double height = Math.abs(clampedCurrentY - initialY);
        double length = Math.min(width, height);

        if (width < height && clampedCurrentY < initialY) {
            clampedCurrentY = clamp(initialY - length, TOP_BOARD, BOTTOM_BOARD);
        } else if (width >= height && clampedCurrentX < initialX) {
           clampedCurrentX = clamp(initialX - length, TOP_BOARD, BOTTOM_BOARD);
        }

        double x = Math.min(initialX, clampedCurrentX);
        double y = Math.min(initialY, clampedCurrentY);


        if (!isCreatedSelectionRectangle) {
            createSelectionRectangle(x, y, length);
            isCreatedSelectionRectangle = true;
        } else {
            updateSelectionRectangle(x, y, length);
        }
    }

    private void createSelectionRectangle(double x, double y, double length) {
        selection = new Rectangle(x, y, length, length);
        selection.setFill(Color.AQUA.deriveColor(0, 1, 1, 0.3));
        selection.setStroke(Color.AQUA);
        pane.getChildren().add(selection);

        this.topLeftCorner = new Point(x, y);
        this.bottomRightCorner = new Point(x + length, y + length);
    }

    private void updateSelectionRectangle(double x, double y, double length) {
        selection.setX(x);
        selection.setY(y);
        selection.setWidth(length);
        selection.setHeight(length);

        this.topLeftCorner = new Point(x, y);
        this.bottomRightCorner = new Point(x + length, y + length);
    }

    public void deleteSelectionRectangle() {
        if (selection != null) {
            pane.getChildren().remove(selection);
            selection = null;
        }
        isCreatedSelectionRectangle = false;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    public double getInitialX() { return initialX; }
    public double getInitialY() { return initialY; }

    public Point getTopLeftCorner() { return topLeftCorner; }
    public Point getBottomRightCorner() { return bottomRightCorner; }

    public Point getBottomLeftCorner() {
        return new Point(topLeftCorner.getX(), bottomRightCorner.getY());
    }

    public Point getTopRightCorner() {
        return new Point(bottomRightCorner.getX(), topLeftCorner.getY());
    }
}