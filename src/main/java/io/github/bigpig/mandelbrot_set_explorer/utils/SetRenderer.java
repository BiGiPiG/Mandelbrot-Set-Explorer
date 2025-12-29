package io.github.bigpig.mandelbrot_set_explorer.utils;

import io.github.bigpig.mandelbrot_set_explorer.Configuration.Configuration;
import io.github.bigpig.mandelbrot_set_explorer.set_builders.ISetBuilder;
import io.github.bigpig.mandelbrot_set_explorer.set_builders.SimpleSetBuilder;
import javafx.scene.image.WritableImage;

public class SetRenderer {

    private ComplexNumber currentBottomLeft;
    private ComplexNumber currentTopRight;
    private final int width, height;
    private final int maxIter;

    public SetRenderer(int width, int height, int maxIter) {
        this.width = width;
        this.height = height;
        this.maxIter = maxIter;
        reset();
    }

    public void reset() {
        this.currentBottomLeft = Configuration.INITIAL_BOTTOM_LEFT;
        this.currentTopRight = Configuration.INITIAL_TOP_RIGHT;
    }

    public void render(WritableImage area) {
        ISetBuilder builder = new SimpleSetBuilder(width, height, maxIter);
        builder.build(area, currentBottomLeft, currentTopRight);
    }

    public void zoomTo(Point pBottomLeft, Point pTopRight) {
        ComplexNumber bottomLeft = computeComplexNumber(pBottomLeft);
        ComplexNumber topRight = computeComplexNumber(pTopRight);
        this.currentBottomLeft = bottomLeft;
        this.currentTopRight = topRight;
    }

    ComplexNumber computeComplexNumber(Point point) {
        double re = currentBottomLeft.getX() + (currentTopRight.getX() - currentBottomLeft.getX()) * point.getX() / width;
        double im = currentTopRight.getY() - (currentTopRight.getY() - currentBottomLeft.getY()) * point.getY() / height;
        return new ComplexNumber(re, im);
    }
}
