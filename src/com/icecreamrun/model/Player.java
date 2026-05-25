package com.icecreamrun.model;

import com.icecreamrun.game.GameConfig;

import java.awt.Rectangle;

public class Player {
    private final int x;
    private final int width;
    private final int height;
    private double yOffset;
    private double verticalVelocity;
    private boolean onGround;

    public Player(int x, int yOffset, int width, int height) {
        this.x = x;
        this.yOffset = yOffset;
        this.width = width;
        this.height = height;
        this.onGround = true;
    }

    public void jump() {
        if (!onGround) {
            return;
        }

        verticalVelocity = GameConfig.JUMP_SPEED;
        onGround = false;
    }

    public void update() {
        if (onGround) {
            return;
        }

        yOffset += verticalVelocity;
        verticalVelocity += GameConfig.GRAVITY;

        if (yOffset >= 0) {
            yOffset = 0;
            verticalVelocity = 0;
            onGround = true;
        }
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getYOffset() {
        return (int) Math.round(yOffset);
    }

    public Rectangle getBounds(int groundY) {
        int y = groundY - height + getYOffset();
        return new Rectangle(x, y, width, height);
    }
}
