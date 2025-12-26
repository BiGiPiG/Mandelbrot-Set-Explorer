package io.github.bigpig.mandelbrot_set_explorer.utils;

import javafx.scene.image.WritableImage;

public interface ISetBuilder {

    ComplexNumber bottomLeftPoint = new ComplexNumber(-2.0, -1.5);
    ComplexNumber topRightPoint = new ComplexNumber(1.0, 1.5);

    void build(WritableImage image, ComplexNumber topLeft, ComplexNumber bottomRight);

    static ComplexNumber computeBorderComplexNumber(Point point, int width, int height) {
        double cx = bottomLeftPoint.getX() + (topRightPoint.getX() - bottomLeftPoint.getX()) * point.getX() / width;
        double cy = topRightPoint.getY() - (topRightPoint.getY() - bottomLeftPoint.getY()) * point.getY() / height;
        return new ComplexNumber(cx, cy);
    }
}
