package com.icecreamrun.model;

import com.icecreamrun.game.GameConfig;

import java.awt.Rectangle;

public class IceCreamItem extends MovingItem {
    public IceCreamItem(double x, int width, int height) {
        super(x, width, height);
    }

    @Override
    public Rectangle getBounds(int groundY) {
        int y = groundY - GameConfig.ICE_CREAM_HEIGHT_ABOVE_GROUND;
        return new Rectangle(getX(), y, getWidth(), getHeight());
    }
}
