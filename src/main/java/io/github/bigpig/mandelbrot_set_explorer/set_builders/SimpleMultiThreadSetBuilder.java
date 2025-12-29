package io.github.bigpig.mandelbrot_set_explorer.set_builders;

import io.github.bigpig.mandelbrot_set_explorer.utils.ComplexNumber;
import io.github.bigpig.mandelbrot_set_explorer.utils.Point;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class SimpleMultiThreadSetBuilder implements ISetBuilder {

    private static final double ESCAPE_RADIUS_SQUARED = 4.0;
    private static final int SUPER_SAMPLING_FACTOR = 2;
    private static final int COUNT_THREADS = 5;

    private final int width;
    private final int height;
    private final int maxIterCount;

    private final Color[][] colors;

    public SimpleMultiThreadSetBuilder(int width, int height, int maxIterCount) {
        this.width = width;
        this.height = height;
        this.maxIterCount = maxIterCount;
        this.colors = new Color[width][height];
    }

    @Override
    public void build(WritableImage image, ComplexNumber bottomLeft, ComplexNumber topRight) {
        List<Thread> threads = makeThreads(bottomLeft, topRight);
        startThreads(threads);
        waitAllThreads(threads);
        printSet(image);
    }

    private List<Thread> makeThreads(ComplexNumber bottomLeftPoint, ComplexNumber topRightPoint) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < COUNT_THREADS; i++) {
            final int startY = i * 100;
            final int endY = i * 100 + 100;
            Thread thread = new Thread(() -> buildTask(startY, endY, maxIterCount, bottomLeftPoint, topRightPoint),
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
                           ComplexNumber bottomLeftPoint, ComplexNumber topRightPoint) {

        for (int y = y0; y < y1; y++) {
            for (int x = 0; x < width; x++) {
                double rSum = 0, gSum = 0, bSum = 0;
                for (int sy = 0; sy < SUPER_SAMPLING_FACTOR; sy++) {
                    for (int sx = 0; sx < SUPER_SAMPLING_FACTOR; sx++) {
                        Point pixel = new Point(
                                x + (sx + 0.5) / SUPER_SAMPLING_FACTOR,
                                y + (sy + 0.5) / SUPER_SAMPLING_FACTOR
                        );
                        ComplexNumber c = pointToComplexNumber(pixel, bottomLeftPoint, topRightPoint);

                        int iter = getIter(maxIterCount, c);
                        Color color = computeColor(iter);

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
            }
        }
    }

    private ComplexNumber pointToComplexNumber(Point point, ComplexNumber bottomLeft, ComplexNumber topRight) {
        double re = bottomLeft.getX() + (topRight.getX() - bottomLeft.getX()) * point.getX() / width;
        double im = topRight.getY() - (topRight.getY() - bottomLeft.getY()) * point.getY() / height;
        return new ComplexNumber(re, im);
    }

    private static int getIter(int maxIter, ComplexNumber c) {
        ComplexNumber z = new ComplexNumber(0.0, 0.0);
        int iter = 0;
        while (z.getX() * z.getX() + z.getY() * z.getY() <= ESCAPE_RADIUS_SQUARED && iter < maxIter) {
            double x = z.getX();
            double y = z.getY();
            z.setX(x * x - y * y + c.getX());
            z.setY(2 * x * y + c.getY());
            iter++;
        }
        return iter;
    }

    private Color computeColor(int iterCount) {
        if (iterCount == maxIterCount) {
            return Color.BLACK;
        } else {
            double t = (double) iterCount / maxIterCount;
            double r = 9 * (1 - t) * t * t * t;
            double g = 15 * (1 - t) * (1 - t) * t * t;
            double b = 8.5 * (1 - t) * (1 - t) * (1 - t) * t;
            return Color.rgb(
                    (int) Math.min(255, r * 255),
                    (int) Math.min(255, g * 255),
                    (int) Math.min(255, b * 255)
            );
        }
    }
}
