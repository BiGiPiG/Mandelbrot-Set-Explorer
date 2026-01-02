module io.github.bigpig.mandelbrot_set_explorer {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;


    opens io.github.bigpig.mandelbrot_set_explorer to javafx.fxml;
    exports io.github.bigpig.mandelbrot_set_explorer;
    exports io.github.bigpig.mandelbrot_set_explorer.utils;
    exports io.github.bigpig.mandelbrot_set_explorer.set_builders;
}