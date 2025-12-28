package io.github.bigpig.mandelbrot_set_explorer.utils;

import javafx.scene.image.WritableImage;

public interface ISetBuilder {
    void build(WritableImage image, ComplexNumber bottomLeft, ComplexNumber topRight);
}
