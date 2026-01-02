package io.github.bigpig.mandelbrot_set_explorer.configuration;

import io.github.bigpig.mandelbrot_set_explorer.MainApplication;
import io.github.bigpig.mandelbrot_set_explorer.utils.ComplexNumber;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;

public class Configuration {
    public static final int WORK_AREA_WIDTH = 500;
    public static final int WORK_AREA_HEIGHT = 500;

    public static final int BUTTON_WIDTH = 100;
    public static final int BUTTON_HEIGHT = 30;
    public static final int BUTTON_Y_SHIFT = 510;
    public static final int MAX_ITER_COUNT = 5000;

    public static final ComplexNumber INITIAL_BOTTOM_LEFT = new ComplexNumber(-2.0, -1.5);
    public static final ComplexNumber INITIAL_TOP_RIGHT = new ComplexNumber(1.0, 1.5);

    public static ImageView createWorkArea(WritableImage image) {
        ImageView imageView = new ImageView(image);
        imageView.setX(10);
        imageView.setY(10);
        return imageView;
    }

    public static Button createBackButton(Runnable operation) {
        Button button = new Button();
        button.setText("Back");
        button.setPrefWidth(BUTTON_WIDTH);
        button.setPrefHeight(BUTTON_HEIGHT);
        button.setLayoutY(BUTTON_Y_SHIFT + (MainApplication.SCENE_HEIGHT - BUTTON_Y_SHIFT - BUTTON_HEIGHT) / 2.0);
        button.setLayoutX((MainApplication.SCENE_WIDTH - BUTTON_WIDTH) / 2.0);
        button.getStyleClass().add("back-button");
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> operation.run());
        return button;
    }
}
