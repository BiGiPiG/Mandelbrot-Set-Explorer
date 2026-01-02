package io.github.bigpig.mandelbrot_set_explorer.set_builders;

import io.github.bigpig.mandelbrot_set_explorer.utils.BuilderUtils;
import io.github.bigpig.mandelbrot_set_explorer.utils.ComplexNumber;
import io.github.bigpig.mandelbrot_set_explorer.utils.Point;
import io.github.bigpig.mandelbrot_set_explorer.utils.ProgressCallback;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class SimpleSetBuilder implements ISetBuilder {

    private static final int SUPER_SAMPLING_FACTOR = 2;

    private final int width;
    private final int height;
    private final int maxIterCount;
    private final BuilderUtils builderUtils;
    private int pixelCount;
    private final int totalPixelsCount;

    public SimpleSetBuilder(int width, int height, int maxIterCount, BuilderUtils builderUtils) {
        this.width = width;
        this.height = height;
        this.maxIterCount = maxIterCount;
        this.builderUtils = builderUtils;
        this.totalPixelsCount = width * height;
    }

    @Override
    public void build(WritableImage image, ComplexNumber bottomLeftPoint, ComplexNumber topRightPoint, ProgressCallback callback) {

        PixelWriter writer = image.getPixelWriter();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double rSum = 0, gSum = 0, bSum = 0;
                for (int sy = 0; sy < SUPER_SAMPLING_FACTOR; sy++) {
                    for (int sx = 0; sx < SUPER_SAMPLING_FACTOR; sx++) {
                        Point pixel = new Point(
                                x + (sx + 0.5) / SUPER_SAMPLING_FACTOR,
                                y + (sy + 0.5) / SUPER_SAMPLING_FACTOR
                        );
                        ComplexNumber c = builderUtils.pointToComplexNumber(pixel, bottomLeftPoint, topRightPoint);

                        int iter = builderUtils.getIter(maxIterCount, c);
                        Color color = builderUtils.computeColor(iter, maxIterCount);

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
                incrementPixelCount(callback);
            }
        }
    }

    private void incrementPixelCount(ProgressCallback callback) {
        pixelCount++;
        if (pixelCount % 2500 == 0) callback.onProgress((double) pixelCount / totalPixelsCount);
    }
}