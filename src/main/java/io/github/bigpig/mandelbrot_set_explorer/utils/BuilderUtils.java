package io.github.bigpig.mandelbrot_set_explorer.utils;

import javafx.scene.paint.Color;

public class BuilderUtils {

    private static final double ESCAPE_RADIUS_SQUARED = 4.0;

    private final int width;
    private final int height;


    public BuilderUtils(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public ComplexNumber pointToComplexNumber(Point point, ComplexNumber bottomLeft, ComplexNumber topRight) {
        double re = bottomLeft.getX() + (topRight.getX() - bottomLeft.getX()) * point.getX() / width;
        double im = topRight.getY() - (topRight.getY() - bottomLeft.getY()) * point.getY() / height;
        return new ComplexNumber(re, im);
    }

    public int getIter(int maxIter, ComplexNumber c) {
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

    public Color computeColor(int iterCount, int maxIterCount) {
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
