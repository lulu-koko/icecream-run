package com.icecreamrun.model;

import java.awt.Rectangle;

public class BananaPeel extends MovingItem {
    public BananaPeel(double x, int width, int height) {
        super(x, width, height);
    }

    @Override
    public Rectangle getBounds(int groundY) {
        return new Rectangle(getX(), groundY - getHeight(), getWidth(), getHeight());
    }
}
