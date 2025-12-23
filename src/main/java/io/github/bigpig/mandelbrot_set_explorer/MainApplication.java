package io.github.bigpig.mandelbrot_set_explorer;

import io.github.bigpig.mandelbrot_set_explorer.Configuration.Configuration;
import io.github.bigpig.mandelbrot_set_explorer.handlers.SelectionHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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

    public static void printFractal(WritableImage image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        int maxIter = 1000;

        double minX = -2.0, maxX = 1.0;
        double minY = -1.5, maxY = 1.5;

        PixelWriter writer = image.getPixelWriter();
        int samples = 2;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double rSum = 0, gSum = 0, bSum = 0;

                for (int sy = 0; sy < samples; sy++) {
                    for (int sx = 0; sx < samples; sx++) {
                        double pixelX = x + (sx + 0.5) / samples;
                        double pixelY = y + (sy + 0.5) / samples;

                        double cx = minX + (maxX - minX) * pixelX / width;
                        double cy = minY + (maxY - minY) * pixelY / height;

                        double zx = 0.0, zy = 0.0;
                        int iter = 0;
                        while (zx * zx + zy * zy <= 4.0 && iter < maxIter) {
                            double zxNew = zx * zx - zy * zy + cx;
                            zy = 2 * zx * zy + cy;
                            zx = zxNew;
                            iter++;
                        }

                        Color color;
                        if (iter == maxIter) {
                            color = Color.BLACK;
                        } else {
                            double t = (double) iter / maxIter;
                            double r = 9 * (1 - t) * t * t * t;
                            double g = 15 * (1 - t) * (1 - t) * t * t;
                            double b = 8.5 * (1 - t) * (1 - t) * (1 - t) * t;
                            color = Color.rgb(
                                    (int) Math.min(255, r * 255),
                                    (int) Math.min(255, g * 255),
                                    (int) Math.min(255, b * 255)
                            );
                        }

                        rSum += color.getRed();
                        gSum += color.getGreen();
                        bSum += color.getBlue();
                    }
                }

                int sampleCount = samples * samples;
                Color avgColor = Color.color(
                        rSum / sampleCount,
                        gSum / sampleCount,
                        bSum / sampleCount
                );

                writer.setColor(x, y, avgColor);
            }
        }
    }
}