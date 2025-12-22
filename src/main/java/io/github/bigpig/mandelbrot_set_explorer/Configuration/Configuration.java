package io.github.bigpig.mandelbrot_set_explorer.Configuration;

import io.github.bigpig.mandelbrot_set_explorer.MainApplication;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Configuration {
    public static final int WORK_AREA_WIDTH = 500;
    public static final int WORK_AREA_HEIGHT = 500;

    public static final int BUTTON_WIDTH = 80;

    public static Rectangle createWorkArea(Rectangle workArea) {
        workArea.setWidth(WORK_AREA_WIDTH);
        workArea.setHeight(WORK_AREA_HEIGHT);
        workArea.setX(10);
        workArea.setY(10);
        workArea.setStroke(Color.BLACK);
        return workArea;
    }

    public static Button createBackButton() {
        Button button = new Button();
        button.setText("Back");
        button.setPrefWidth(BUTTON_WIDTH);
        button.setLayoutY(520);
        button.setLayoutX((MainApplication.SCENE_WIDTH - BUTTON_WIDTH) / 2.0);
        return button;
    }
}
