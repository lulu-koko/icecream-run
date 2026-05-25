package com.icecreamrun.model;

import java.awt.Rectangle;

public abstract class MovingItem {
    private double x;
    private final int width;
    private final int height;

    protected MovingItem(double x, int width, int height) {
        this.x = x;
        this.width = width;
        this.height = height;
    }

    public void moveLeft(double pixels) {
        x -= pixels;
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }

    public abstract Rectangle getBounds(int groundY);

    public int getX() {
        return (int) Math.round(x);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
