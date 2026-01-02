package io.github.bigpig.mandelbrot_set_explorer;

import io.github.bigpig.mandelbrot_set_explorer.Configuration.Configuration;
import io.github.bigpig.mandelbrot_set_explorer.handlers.SelectionHandler;
import io.github.bigpig.mandelbrot_set_explorer.utils.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainApplication extends Application {
    public static final int SCENE_WIDTH = 520;
    public static final int SCENE_HEIGHT = 580;
    public static final String STAGE_TITLE = "Mandelbrot Set Explorer";
    private static final int PROGRESS_BAR_WIDTH = 300;
    private static final int PROGRESS_BAR_HEIGHT = 30;
    private static final int PROGRESS_BAR_Y = 255;

    private final ExecutorService sharedThreadPool =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private WritableImage fractalImage;
    private SetRenderer setRenderer;
    private ProgressBar progressBar;
    private ImageView workArea;

    @Override
    public void start(Stage stage) {
        this.fractalImage = new WritableImage(Configuration.WORK_AREA_WIDTH, Configuration.WORK_AREA_HEIGHT);
        this.setRenderer = new SetRenderer(Configuration.WORK_AREA_WIDTH, Configuration.WORK_AREA_HEIGHT, Configuration.MAX_ITER_COUNT);
        render();

        Scene scene = new Scene(createRootPane(), SCENE_WIDTH, SCENE_HEIGHT);

        scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class
                .getResource("style.css")).toExternalForm());

        stage.setScene(scene);
        stage.setTitle(STAGE_TITLE);
        stage.setResizable(false);
        stage.setOnCloseRequest(_ -> {
            sharedThreadPool.shutdown();
            Platform.exit();
        });
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
                selectionHandler.deleteSelectionRectangle();
                setRenderer.zoomTo(bottomLeftCorner, topRightCorner);
                render();
            }
        });
    }

    private Pane createRootPane() {
        this.workArea = Configuration.createWorkArea(fractalImage);
        setupFractalInteraction(workArea);
        progressBar = createProgressBar();

        Pane pane = new Pane();
        pane.getChildren().addAll(workArea, Configuration.createBackButton(() -> {
            if (setRenderer.setOldBountyPoints()) render();
        }), progressBar);

        return pane;
    }

    private ProgressBar createProgressBar() {
        ProgressBar pb = new ProgressBar();
        pb.setPrefSize(PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT);
        pb.setVisible(false);
        pb.setLayoutX((SCENE_WIDTH - PROGRESS_BAR_WIDTH) / 2.0);
        pb.setLayoutY(PROGRESS_BAR_Y);
        pb.getStyleClass().add("progress-bar");
        return pb;
    }

    private void render() {
        Platform.runLater(() -> {
            workArea.setVisible(false);
            progressBar.setVisible(true);
        });

        new Thread(() -> {
            try {
                setRenderer.renderFull(fractalImage, sharedThreadPool, progress ->
                        Platform.runLater(() -> progressBar.setProgress(progress)));
            } finally {
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    workArea.setVisible(true);
                });
            }
        }).start();
    }
}