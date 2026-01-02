package io.github.bigpig.mandelbrot_set_explorer.set_builders;

import io.github.bigpig.mandelbrot_set_explorer.utils.BuilderUtils;
import io.github.bigpig.mandelbrot_set_explorer.utils.ComplexNumber;
import io.github.bigpig.mandelbrot_set_explorer.utils.Point;
import io.github.bigpig.mandelbrot_set_explorer.utils.ProgressCallback;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

public class MultiThreadSetBuilder implements ISetBuilder {

    private static final int SUPER_SAMPLING_FACTOR = 2;
    private static final int COUNT_THREADS = 5;

    private final int width;
    private final int height;
    private final int maxIterCount;

    private final BuilderUtils builderUtils;
    private final Color[][] colors;
    private final ExecutorService threadPool;

    private int pixelCount;
    private final int totalPixelsCount;

    public MultiThreadSetBuilder(int width, int height, int maxIterCount, BuilderUtils builderUtils, ExecutorService threadPool) {
        this.width = width;
        this.height = height;
        this.maxIterCount = maxIterCount;
        this.builderUtils = builderUtils;
        this.colors = new Color[width][height];
        this.threadPool = threadPool;
        this.totalPixelsCount = width * height;
    }

    @Override
    public void build(WritableImage image, ComplexNumber bottomLeft, ComplexNumber topRight, ProgressCallback callback) {
        var futures = IntStream.range(0, COUNT_THREADS)
                .mapToObj(i -> {
                    int startY = i * 100;
                    int endY = Math.min(startY + 100, height);
                    return threadPool.submit(new Task(startY, endY, bottomLeft, topRight, maxIterCount, callback));
                })
                .toList();

        futures.forEach(future -> {
            try {
                future.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        printSet(image);
    }

    private void printSet(WritableImage image) {
        PixelWriter writer = image.getPixelWriter();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                writer.setColor(i, j, colors[i][j]);
            }
        }
    }

    private class Task implements Runnable {

        private final int startY;
        private final int endY;
        private final ComplexNumber bottomLeftPoint;
        private final ComplexNumber topRightPoint;
        private final int maxIterCount;
        private final ProgressCallback callback;

        public Task(int startY, int endY, ComplexNumber bottomLeftPoint, ComplexNumber topRightPoint, int maxIterCount, ProgressCallback callback) {
            this.startY = startY;
            this.endY = endY;
            this.bottomLeftPoint = bottomLeftPoint;
            this.topRightPoint = topRightPoint;
            this.maxIterCount = maxIterCount;
            this.callback = callback;
        }

        @Override
        public void run() {
            for (int y = startY; y < endY; y++) {
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
    }

    private synchronized void incrementPixelCount(ProgressCallback callback) {
        pixelCount++;
        if (pixelCount % 2500 == 0) callback.onProgress((double) pixelCount / totalPixelsCount);
    }
}
