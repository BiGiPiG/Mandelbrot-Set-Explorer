package io.github.bigpig.mandelbrot_set_explorer.utils;

public class ComplexNumber extends Point {
    public ComplexNumber(double x, double y) {
        super(x, y);
    }
    
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
