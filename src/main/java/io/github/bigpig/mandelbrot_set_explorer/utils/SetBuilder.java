package io.github.bigpig.mandelbrot_set_explorer.utils;

import javafx.scene.image.WritableImage;

public interface SetBuilder {
    public void build(WritableImage image, Point topLeft, Point bottomRight);
}
