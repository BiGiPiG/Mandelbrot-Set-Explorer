package io.github.bigpig.mandelbrot_set_explorer.utils;

import io.github.bigpig.mandelbrot_set_explorer.configuration.Configuration;
import io.github.bigpig.mandelbrot_set_explorer.set_builders.ISetBuilder;
import io.github.bigpig.mandelbrot_set_explorer.set_builders.MultiThreadSetBuilder;
import javafx.scene.image.WritableImage;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutorService;

public class SetRenderer {

    private ComplexNumber currentBottomLeft;
    private ComplexNumber currentTopRight;
    private final int width, height;
    private final int maxIter;
    private final Deque<ComplexNumber> steps;
    private final BuilderUtils builderUtils;

    public SetRenderer(int width, int height, int maxIter) {
        this.width = width;
        this.height = height;
        this.maxIter = maxIter;
        this.builderUtils = new BuilderUtils(width, height);
        steps = new ArrayDeque<>();
        reset();
    }

    public void renderFull(WritableImage area, ExecutorService threadPool, ProgressCallback callback) {
        ISetBuilder builder = new MultiThreadSetBuilder(width, height, maxIter, builderUtils, threadPool);
        builder.build(area, currentBottomLeft, currentTopRight, callback);
    }

    public void reset() {
        this.currentBottomLeft = Configuration.INITIAL_BOTTOM_LEFT;
        this.currentTopRight = Configuration.INITIAL_TOP_RIGHT;
    }

//    public void render(WritableImage area) {
//        BuilderUtils builderUtils = new BuilderUtils(width, height);
//        ISetBuilder builder = new MultiThreadSetBuilder(width, height, maxIter, builderUtils);
//        builder.build(area, currentBottomLeft, currentTopRight);
//    }

    public void zoomTo(Point pBottomLeft, Point pTopRight) {
        ComplexNumber bottomLeft = computeComplexNumber(pBottomLeft);
        ComplexNumber topRight = computeComplexNumber(pTopRight);
        setNewBountyPoints(bottomLeft, topRight);
    }

    ComplexNumber computeComplexNumber(Point point) {
        double re = currentBottomLeft.getX() + (currentTopRight.getX() - currentBottomLeft.getX()) * point.getX() / width;
        double im = currentTopRight.getY() - (currentTopRight.getY() - currentBottomLeft.getY()) * point.getY() / height;
        return new ComplexNumber(re, im);
    }

    private void setNewBountyPoints(ComplexNumber bottomLeft, ComplexNumber topRight) {
        steps.push(currentBottomLeft);
        steps.push(currentTopRight);
        this.currentBottomLeft = bottomLeft;
        this.currentTopRight = topRight;
    }

    public boolean setOldBountyPoints() {
        if (steps.isEmpty()) return false;
        this.currentTopRight = steps.pop();
        this.currentBottomLeft = steps.pop();
        return true;
    }
}
