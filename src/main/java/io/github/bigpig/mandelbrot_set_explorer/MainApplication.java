package io.github.bigpig.mandelbrot_set_explorer;

import io.github.bigpig.mandelbrot_set_explorer.Configuration.Configuration;
import io.github.bigpig.mandelbrot_set_explorer.handlers.SelectionHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApplication extends Application {
    public static final int SCENE_WIDTH = 520;
    public static final int SCENE_HEIGHT = 580;
    public static final String STAGE_TITLE = "Mandelbrot Set Explorer";

    @Override
    public void start(Stage stage) {
        Pane pane = new Pane();
        pane.getChildren().add(Configuration.createWorkArea(new Rectangle()));
        pane.getChildren().add(Configuration.createBackButton());
        Scene scene = new Scene(pane, SCENE_WIDTH, SCENE_HEIGHT);

        SelectionHandler selectionHandler = new SelectionHandler(pane);

        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, event ->
                selectionHandler.setInitialPoint(event.getX(), event.getY()));

        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, event ->
                selectionHandler.makeSelection(event.getX(), event.getY()));

        scene.addEventHandler(MouseEvent.MOUSE_RELEASED, _ ->
                selectionHandler.deleteSelectionRectangle());

        scene.getStylesheets().add(
                Objects.requireNonNull(MainApplication.class.getResource("style.css")).toExternalForm()
        );

        stage.setScene(scene);
        stage.setTitle(STAGE_TITLE);
        stage.setResizable(false);
        stage.show();
    }
}