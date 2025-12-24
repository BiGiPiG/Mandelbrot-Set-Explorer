package io.github.bigpig.mandelbrot_set_explorer.utils;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class SimpleSetBuilder implements ISetBuilder {

    private static final double ESCAPE_RADIUS_SQUARED = 4.0;
    private static final int SUPER_SAMPLING_FACTOR = 2;

    private final int width;
    private final int height;
    private final int maxIterCount;

    public SimpleSetBuilder(int width, int height, int maxIterCount) {
        this.width = width;
        this.height = height;
        this.maxIterCount = maxIterCount;

    }

    @Override
    public void build(WritableImage image, ComplexNumber bottomLeftPoint, ComplexNumber topRightPoint) {

        PixelWriter writer = image.getPixelWriter();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double rSum = 0, gSum = 0, bSum = 0;
                for (int sy = 0; sy < SUPER_SAMPLING_FACTOR; sy++) {
                    for (int sx = 0; sx < SUPER_SAMPLING_FACTOR; sx++) {
                        Point pixel = new Point(x + (sx + 0.5) / SUPER_SAMPLING_FACTOR, y + (sy + 0.5) / SUPER_SAMPLING_FACTOR);
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

                writer.setColor(x, y, avgColor);
            }
        }
    }

    private static int getIter(int maxIter, ComplexNumber c) {
        ComplexNumber z = new ComplexNumber(0.0, 0.0);
        int iter = 0;
        while (z.getX() * z.getX() + z.getY() * z.getY() <= ESCAPE_RADIUS_SQUARED && iter < maxIter) {
            double newX = z.getX();
            double newY = z.getY();
            z.setX(newX * newX - newY * newY + c.getX());
            z.setY(2 * newX * newY + c.getY());
            iter++;
        }
        return iter;
    }

    public ComplexNumber pointToComplexNumber(Point point, ComplexNumber bottomLeftPoint, ComplexNumber topRightPoint) {
        double cx = bottomLeftPoint.getX() + (topRightPoint.getX() - bottomLeftPoint.getX()) * point.getX() / width;
        double cy = topRightPoint.getY() - (topRightPoint.getY() - bottomLeftPoint.getY()) * point.getY() / height;
        return new ComplexNumber(cx, cy);
    }

    public Color computeColor(int iterCount) {
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
