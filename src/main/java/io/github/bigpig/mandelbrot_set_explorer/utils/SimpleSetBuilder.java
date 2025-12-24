package io.github.bigpig.mandelbrot_set_explorer.utils;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class SimpleSetBuilder implements SetBuilder {

    @Override
    public void build(WritableImage image, Point buttomLeftPoint, Point topRightPoint) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        int maxIter = 1000;

        //x0 -> -2.0, y0 -> -1.5
        //x1 -> x, y1 -> y => x = (x1 * (-2.0)) / x0, y = (y1 * (-1.5)) / y0

        PixelWriter writer = image.getPixelWriter();
        int samples = 2;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double rSum = 0, gSum = 0, bSum = 0;

                for (int sy = 0; sy < samples; sy++) {
                    for (int sx = 0; sx < samples; sx++) {
                        double pixelX = x + (sx + 0.5) / samples;
                        double pixelY = y + (sy + 0.5) / samples;

                        double cx = buttomLeftPoint.x() + (topRightPoint.x() - buttomLeftPoint.x()) * pixelX / width;
                        double cy = buttomLeftPoint.y() + (topRightPoint.y() - buttomLeftPoint.y()) * pixelY / height;

                        double zx = 0.0, zy = 0.0;
                        int iter = 0;
                        while (zx * zx + zy * zy <= 4.0 && iter < maxIter) {
                            double zxNew = zx * zx - zy * zy + cx;
                            zy = 2 * zx * zy + cy;
                            zx = zxNew;
                            iter++;
                        }

                        Color color;
                        if (iter == maxIter) {
                            color = Color.BLACK;
                        } else {
                            double t = (double) iter / maxIter;
                            double r = 9 * (1 - t) * t * t * t;
                            double g = 15 * (1 - t) * (1 - t) * t * t;
                            double b = 8.5 * (1 - t) * (1 - t) * (1 - t) * t;
                            color = Color.rgb(
                                    (int) Math.min(255, r * 255),
                                    (int) Math.min(255, g * 255),
                                    (int) Math.min(255, b * 255)
                            );
                        }

                        rSum += color.getRed();
                        gSum += color.getGreen();
                        bSum += color.getBlue();
                    }
                }

                int sampleCount = samples * samples;
                Color avgColor = Color.color(
                        rSum / sampleCount,
                        gSum / sampleCount,
                        bSum / sampleCount
                );

                writer.setColor(x, y, avgColor);
            }
        }
    }
}
