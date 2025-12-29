package io.github.bigpig.mandelbrot_set_explorer.utils;

import io.github.bigpig.mandelbrot_set_explorer.Configuration.Configuration;
import io.github.bigpig.mandelbrot_set_explorer.set_builders.ISetBuilder;
import io.github.bigpig.mandelbrot_set_explorer.set_builders.SimpleSetBuilder;
import javafx.scene.image.WritableImage;

public class SetRenderer {

    private ComplexNumber currentTopLeft;
    private ComplexNumber currentBottomRight;
    private final int width, height;
    private final int maxIter;

    public SetRenderer(int width, int height, int maxIter) {
        this.width = width;
        this.height = height;
        this.maxIter = maxIter;
        reset();
    }

    public void reset() {
        this.currentTopLeft = Configuration.INITIAL_TOP_LEFT;
        this.currentBottomRight = Configuration.INITIAL_BOTTOM_RIGHT;
    }

    public void render(WritableImage area) {
        ISetBuilder builder = new SimpleSetBuilder(width, height, maxIter);
        builder.build(area, currentTopLeft, currentBottomRight);
    }

    public void zoomTo(Point pTopLeft, Point pBottomRight) {
        ComplexNumber topLeft = computeComplexNumber(pTopLeft);
        ComplexNumber bottomRight = computeComplexNumber(pBottomRight);
        this.currentTopLeft = topLeft;
        this.currentBottomRight = bottomRight;
    }

    ComplexNumber computeComplexNumber(Point point) {
        double re = currentTopLeft.getX() + (currentBottomRight.getX() - currentTopLeft.getX()) * point.getX() / width;
        double im = currentTopLeft.getY() - (currentTopLeft.getY() - currentBottomRight.getY()) * point.getY() / height;
        return new ComplexNumber(re, im);
    }
}
