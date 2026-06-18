package com.icecreamrun.game;

import javax.swing.Timer;

public class GameLoop {
    private final Timer timer;

    public GameLoop(Runnable onTick) {
        timer = new Timer(GameConfig.GAME_TICK_MS, event -> onTick.run());
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }
}
