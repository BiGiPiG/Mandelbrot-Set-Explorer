package io.github.bigpig.mandelbrot_set_explorer;

import io.github.bigpig.mandelbrot_set_explorer.Configuration.Configuration;
import io.github.bigpig.mandelbrot_set_explorer.handlers.SelectionHandler;
import io.github.bigpig.mandelbrot_set_explorer.utils.ComplexNumber;
import io.github.bigpig.mandelbrot_set_explorer.utils.ISetBuilder;
import io.github.bigpig.mandelbrot_set_explorer.utils.Point;
import io.github.bigpig.mandelbrot_set_explorer.utils.SimpleSetBuilder;
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

    @Override
    public void start(Stage stage) {
        Pane pane = new Pane();
        WritableImage fractalImage = new WritableImage(Configuration.WORK_AREA_WIDTH, Configuration.WORK_AREA_HEIGHT);
        printFractal(fractalImage);
        ImageView workArea = Configuration.createWorkArea(fractalImage);
        pane.getChildren().add(workArea);
        pane.getChildren().add(Configuration.createBackButton());
        Scene scene = new Scene(pane, SCENE_WIDTH, SCENE_HEIGHT);

        addSceneEvents(scene, pane);
        scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class
                .getResource("style.css")).toExternalForm());

        stage.setScene(scene);
        stage.setTitle(STAGE_TITLE);
        stage.setResizable(false);
        stage.show();
    }

    public static void printFractal(WritableImage image) {
        Point leftTop = new Point(0, 0);
        Point rightBottom = new Point(500, 500);
        ISetBuilder setBuilder = new SimpleSetBuilder(Configuration.WORK_AREA_WIDTH, Configuration.WORK_AREA_HEIGHT, 1000);

        ComplexNumber bottomLeftPoint = setBuilder.computeBorderComplexNumber(leftTop,
                Configuration.WORK_AREA_WIDTH, Configuration.WORK_AREA_HEIGHT);

        ComplexNumber topRightPoint = setBuilder.computeBorderComplexNumber(rightBottom,
                Configuration.WORK_AREA_WIDTH, Configuration.WORK_AREA_HEIGHT);
        setBuilder.build(image, bottomLeftPoint, topRightPoint);
    }

    public void addSceneEvents(Scene scene, Pane pane) {
        SelectionHandler selectionHandler = new SelectionHandler(pane);

        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, event ->
                selectionHandler.setInitialPoint(event.getX(), event.getY()));
        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, event ->
                selectionHandler.makeSelection(event.getX(), event.getY()));
        scene.addEventHandler(MouseEvent.MOUSE_RELEASED, _ ->
                selectionHandler.deleteSelectionRectangle());

    }
}