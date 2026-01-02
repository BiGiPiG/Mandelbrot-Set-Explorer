package io.github.bigpig.mandelbrot_set_explorer.utils;

@FunctionalInterface
public interface ProgressCallback {
    void onProgress(double progress);
}
