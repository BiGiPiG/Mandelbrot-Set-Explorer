package io.github.bigpig.mandelbrot_set_explorer;

import io.github.bigpig.mandelbrot_set_explorer.Configuration.Configuration;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class MainApplication extends Application {
    public static final int SCENE_WIDTH = 520;
    public static final int SCENE_HEIGHT = 555;
    public static final String STAGE_TITLE = "Mandelbrot Set Explorer";

    @Override
    public void start(Stage stage) {
        Pane pane = new Pane();
        pane.getChildren().add(Configuration.createWorkArea(new Rectangle()));
        pane.getChildren().add(Configuration.createBackButton());
        Scene scene = new Scene(pane, SCENE_WIDTH, SCENE_HEIGHT);

        stage.setScene(scene);
        stage.setTitle(STAGE_TITLE);
        stage.setResizable(false);
        stage.show();
    }
}