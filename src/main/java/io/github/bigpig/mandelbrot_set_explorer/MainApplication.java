package io.github.bigpig.mandelbrot_set_explorer;

import io.github.bigpig.mandelbrot_set_explorer.Configuration.Configuration;
import io.github.bigpig.mandelbrot_set_explorer.handlers.SelectionHandler;
import io.github.bigpig.mandelbrot_set_explorer.utils.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApplication extends Application {
    public static final int SCENE_WIDTH = 520;
    public static final int SCENE_HEIGHT = 580;
    public static final String STAGE_TITLE = "Mandelbrot Set Explorer";
    public WritableImage fractalImage;
    private SetRenderer setRenderer;

    @Override
    public void start(Stage stage) {
        Pane pane = new Pane();
        this.fractalImage = new WritableImage(Configuration.WORK_AREA_WIDTH, Configuration.WORK_AREA_HEIGHT);
        this.setRenderer = new SetRenderer(Configuration.WORK_AREA_WIDTH, Configuration.WORK_AREA_HEIGHT, Configuration.MAX_ITER_COUNT);
        setRenderer.render(fractalImage);

        ImageView workArea = Configuration.createWorkArea(fractalImage);
        pane.getChildren().add(workArea);
        pane.getChildren().add(Configuration.createBackButton());
        Scene scene = new Scene(createRootPane(), SCENE_WIDTH, SCENE_HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class
                .getResource("style.css")).toExternalForm());

        stage.setScene(scene);
        stage.setTitle(STAGE_TITLE);
        stage.setResizable(false);
        stage.show();
    }

    public void setupFractalInteraction(ImageView workArea) {
        SelectionHandler selectionHandler = new SelectionHandler((Pane) workArea.getParent());

        workArea.addEventHandler(MouseEvent.MOUSE_PRESSED, event ->
                selectionHandler.setInitialPoint(event.getX(), event.getY()));
        workArea.addEventHandler(MouseEvent.MOUSE_DRAGGED, event ->
                selectionHandler.makeSelection(event.getX(), event.getY()));
        workArea.addEventHandler(MouseEvent.MOUSE_RELEASED, _ -> {
            Point bottomLeftCorner = selectionHandler.getBottomLeftCorner();
            Point topRightCorner = selectionHandler.getTopRightCorner();
            if (bottomLeftCorner != null && !bottomLeftCorner.equals(topRightCorner)) {
                System.out.println("zoom");
                setRenderer.zoomTo(bottomLeftCorner, topRightCorner);
                setRenderer.render(fractalImage);
            }
            selectionHandler.deleteSelectionRectangle();
        });
    }

    private Pane createRootPane() {
        Pane pane = new Pane();
        ImageView workArea = Configuration.createWorkArea(fractalImage);
        pane.getChildren().addAll(workArea, Configuration.createBackButton());
        setupFractalInteraction(workArea);
        return pane;
    }
}