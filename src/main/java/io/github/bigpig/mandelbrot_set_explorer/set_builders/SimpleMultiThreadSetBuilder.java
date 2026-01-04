package io.github.bigpig.mandelbrot_set_explorer.set_builders;

import io.github.bigpig.mandelbrot_set_explorer.utils.BuilderUtils;
import io.github.bigpig.mandelbrot_set_explorer.utils.ComplexNumber;
import io.github.bigpig.mandelbrot_set_explorer.utils.Point;
import io.github.bigpig.mandelbrot_set_explorer.utils.ProgressCallback;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class SimpleMultiThreadSetBuilder implements ISetBuilder {

    private static final int SUPER_SAMPLING_FACTOR = 2;
    private static final int COUNT_THREADS = 500;

    private final int width;
    private final int height;
    private final int maxIterCount;

    private final BuilderUtils builderUtils;
    private int pixelCount;
    private final int totalPixelsCount;

    private final Color[][] colors;

    public SimpleMultiThreadSetBuilder(int width, int height, int maxIterCount, BuilderUtils builderUtils) {
        this.width = width;
        this.height = height;
        this.maxIterCount = maxIterCount;
        this.builderUtils = builderUtils;
        this.colors = new Color[width][height];
        this.totalPixelsCount = width * height;
    }

    @Override
    public void build(WritableImage image, ComplexNumber bottomLeft, ComplexNumber topRight, ProgressCallback callback) {
        List<Thread> threads = makeThreads(bottomLeft, topRight, callback);
        startThreads(threads);
        waitAllThreads(threads);
        printSet(image);
    }

    private List<Thread> makeThreads(ComplexNumber bottomLeftPoint, ComplexNumber topRightPoint, ProgressCallback callback) {
        List<Thread> threads = new ArrayList<>();
        int stripeWidth = height / COUNT_THREADS;
        for (int i = 0; i < COUNT_THREADS; i++) {
            final int startY = i * stripeWidth;
            final int endY = i * stripeWidth + stripeWidth;
            Thread thread = new Thread(() -> buildTask(startY, endY, maxIterCount, bottomLeftPoint, topRightPoint, callback),
                    "Mandelbrot-Worker-" + i);
            threads.add(thread);
        }

        return threads;
    }

    public void startThreads(List<Thread> threads) {
        for (Thread thread : threads) {
            thread.start();
        }
    }

    private void waitAllThreads(List<Thread> threads) {
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void printSet(WritableImage image) {
        PixelWriter writer = image.getPixelWriter();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                writer.setColor(i, j, colors[i][j]);
            }
        }
    }

    private void buildTask(int y0, int y1, int maxIterCount,
                           ComplexNumber bottomLeftPoint, ComplexNumber topRightPoint, ProgressCallback callback) {

        for (int y = y0; y < y1; y++) {
            for (int x = 0; x < width; x++) {
                double rSum = 0, gSum = 0, bSum = 0;
                for (int sy = 0; sy < SUPER_SAMPLING_FACTOR; sy++) {
                    for (int sx = 0; sx < SUPER_SAMPLING_FACTOR; sx++) {
                        Point pixel = new Point(
                                x + (sx + 0.5) / SUPER_SAMPLING_FACTOR,
                                y + (sy + 0.5) / SUPER_SAMPLING_FACTOR
                        );
                        ComplexNumber c = builderUtils.pointToComplexNumber(pixel, bottomLeftPoint, topRightPoint);

                        int iter = builderUtils.getIter(maxIterCount, c);
                        Color color = builderUtils.computeColor(iter, maxIterCount);

                        rSum += color.getRed();
                        gSum += color.getGreen();
                        bSum += color.getBlue();
                    }
                }

                int sampleCount = SUPER_SAMPLING_FACTOR * SUPER_SAMPLING_FACTOR;
                Color avgColor = Color.color(
                        rSum / sampleCount,
                        gSum / sampleCount,
                        bSum / sampleCount
                );
                colors[x][y] = avgColor;
                incrementPixelCount(callback);
            }
        }
    }

    private synchronized void incrementPixelCount(ProgressCallback callback) {
        pixelCount++;
        if (pixelCount % 2500 == 0) callback.onProgress((double) pixelCount / totalPixelsCount);
    }
}
