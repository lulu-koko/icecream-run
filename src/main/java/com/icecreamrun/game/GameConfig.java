package com.icecreamrun.game;

public final class GameConfig {
    public static final int TARGET_DISTANCE_METERS = 1000;
    public static final int MAX_BRAIN_FREEZE = 100;
    public static final int MAX_BANANA_HITS = 3;
    public static final int GAME_TICK_MS = 16;
    public static final double BASE_SPEED_METERS_PER_SECOND = 50.0;
    public static final double BOOST_SPEED_MULTIPLIER = 1.75;
    public static final int BOOST_DURATION_MS = 1700;
    public static final int ICE_CREAM_BRAIN_FREEZE = 32;
    public static final double BRAIN_FREEZE_DECAY_PER_SECOND = 6.0;

    public static final int PLAYER_WIDTH = 42;
    public static final int PLAYER_HEIGHT = 64;
    public static final double JUMP_SPEED = -13.5;
    public static final double GRAVITY = 0.65;

    public static final int BANANA_WIDTH = 42;
    public static final int BANANA_HEIGHT = 18;
    public static final int ICE_CREAM_WIDTH = 30;
    public static final int ICE_CREAM_HEIGHT = 42;
    public static final double ITEM_SPEED_PIXELS_PER_SECOND = 305.0;
    public static final int ICE_CREAM_HEIGHT_ABOVE_GROUND = 135;
    public static final int MIN_BANANA_SPAWN_MS = 950;
    public static final int MAX_BANANA_SPAWN_MS = 1850;
    public static final int MIN_ICE_CREAM_SPAWN_MS = 1250;
    public static final int MAX_ICE_CREAM_SPAWN_MS = 2400;

    private GameConfig() {
    }
}
