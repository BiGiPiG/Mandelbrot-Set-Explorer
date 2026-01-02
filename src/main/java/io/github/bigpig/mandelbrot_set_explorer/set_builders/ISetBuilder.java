package io.github.bigpig.mandelbrot_set_explorer.set_builders;

import io.github.bigpig.mandelbrot_set_explorer.utils.ComplexNumber;
import io.github.bigpig.mandelbrot_set_explorer.utils.ProgressCallback;
import javafx.scene.image.WritableImage;

public interface ISetBuilder {
    void build(WritableImage image, ComplexNumber bottomLeft, ComplexNumber topRight, ProgressCallback callback);

    default void build(WritableImage image, ComplexNumber bottomLeft, ComplexNumber topRight) {
        build(image, bottomLeft, topRight, p -> {});
    }
}
