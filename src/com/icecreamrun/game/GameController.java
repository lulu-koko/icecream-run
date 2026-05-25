package com.icecreamrun.game;

import com.icecreamrun.data.FailReason;
import com.icecreamrun.data.GameResult;
import com.icecreamrun.data.GameResultType;
import com.icecreamrun.model.BananaPeel;
import com.icecreamrun.model.IceCreamItem;

import java.util.Iterator;
import java.util.Random;

public class GameController {
    private final GameState gameState;
    private final CollisionDetector collisionDetector;
    private final Random random;
    private long startTimeMillis;
    private long lastUpdateMillis;
    private long nextBananaSpawnMillis;
    private long nextIceCreamSpawnMillis;
    private boolean running;
    private int playAreaWidth;
    private int groundY;

    public GameController(GameState gameState) {
        this.gameState = gameState;
        this.collisionDetector = new CollisionDetector();
        this.random = new Random();
    }

    public void startNewGame() {
        gameState.reset();
        startTimeMillis = System.currentTimeMillis();
        lastUpdateMillis = startTimeMillis;
        scheduleNextBanana(startTimeMillis);
        scheduleNextIceCream(startTimeMillis);
        running = true;
    }

    public void stop() {
        running = false;
    }

    public void jump() {
        if (running) {
            gameState.getPlayer().jump();
        }
    }

    public GameResult update(int playAreaWidth, int groundY) {
        if (!running) {
            return null;
        }

        this.playAreaWidth = playAreaWidth;
        this.groundY = groundY;
        if (playAreaWidth <= 0 || groundY <= 0) {
            return null;
        }

        long now = System.currentTimeMillis();
        double deltaSeconds = (now - lastUpdateMillis) / 1000.0;
        lastUpdateMillis = now;

        gameState.getPlayer().update();
        gameState.decayBrainFreeze(deltaSeconds);

        double speedMultiplier = gameState.isBoostActive(now) ? GameConfig.BOOST_SPEED_MULTIPLIER : 1.0;
        gameState.addDistance(GameConfig.BASE_SPEED_METERS_PER_SECOND * speedMultiplier * deltaSeconds);
        gameState.setElapsedSeconds((int) ((now - startTimeMillis) / 1000));
        spawnItemsIfNeeded(now);
        moveItems(deltaSeconds, speedMultiplier);

        GameResult failResult = handleCollisions(now);
        if (failResult != null) {
            return failResult;
        }

        if (gameState.getBrainFreeze() >= GameConfig.MAX_BRAIN_FREEZE) {
            return finish(GameResultType.FAIL, FailReason.BRAIN_FREEZE);
        }

        if (gameState.getDistanceMeters() >= GameConfig.TARGET_DISTANCE_METERS) {
            return finish(GameResultType.WIN, FailReason.NONE);
        }

        return null;
    }

    public boolean isRunning() {
        return running;
    }

    private void spawnItemsIfNeeded(long now) {
        if (now >= nextBananaSpawnMillis) {
            gameState.getBananaPeels().add(new BananaPeel(playAreaWidth + 30,
                    GameConfig.BANANA_WIDTH, GameConfig.BANANA_HEIGHT));
            scheduleNextBanana(now);
        }

        if (now >= nextIceCreamSpawnMillis) {
            gameState.getIceCreamItems().add(new IceCreamItem(playAreaWidth + 30,
                    GameConfig.ICE_CREAM_WIDTH, GameConfig.ICE_CREAM_HEIGHT));
            scheduleNextIceCream(now);
        }
    }

    private void moveItems(double deltaSeconds, double speedMultiplier) {
        double movePixels = GameConfig.ITEM_SPEED_PIXELS_PER_SECOND * speedMultiplier * deltaSeconds;
        gameState.getBananaPeels().forEach(item -> item.moveLeft(movePixels));
        gameState.getIceCreamItems().forEach(item -> item.moveLeft(movePixels));

        gameState.getBananaPeels().removeIf(BananaPeel::isOffScreen);
        gameState.getIceCreamItems().removeIf(IceCreamItem::isOffScreen);
    }

    private GameResult handleCollisions(long now) {
        Iterator<BananaPeel> bananaIterator = gameState.getBananaPeels().iterator();
        while (bananaIterator.hasNext()) {
            BananaPeel bananaPeel = bananaIterator.next();
            if (collisionDetector.isColliding(gameState.getPlayer(), bananaPeel, groundY)) {
                bananaIterator.remove();
                gameState.addBananaHit();

                if (gameState.getBananaHits() >= GameConfig.MAX_BANANA_HITS) {
                    return finish(GameResultType.FAIL, FailReason.BANANA);
                }
            }
        }

        Iterator<IceCreamItem> iceCreamIterator = gameState.getIceCreamItems().iterator();
        while (iceCreamIterator.hasNext()) {
            IceCreamItem iceCreamItem = iceCreamIterator.next();
            if (collisionDetector.isColliding(gameState.getPlayer(), iceCreamItem, groundY)) {
                iceCreamIterator.remove();
                gameState.eatIceCream(now);
            }
        }

        return null;
    }

    private GameResult finish(GameResultType type, FailReason failReason) {
        running = false;
        return new GameResult(type, failReason, gameState.getDistanceMeters(),
                gameState.getElapsedSeconds(), gameState.getIceCreamCount(),
                gameState.getBananaHits(), gameState.getMaxBrainFreeze());
    }

    private void scheduleNextBanana(long now) {
        nextBananaSpawnMillis = now + randomRange(GameConfig.MIN_BANANA_SPAWN_MS,
                GameConfig.MAX_BANANA_SPAWN_MS);
    }

    private void scheduleNextIceCream(long now) {
        nextIceCreamSpawnMillis = now + randomRange(GameConfig.MIN_ICE_CREAM_SPAWN_MS,
                GameConfig.MAX_ICE_CREAM_SPAWN_MS);
    }

    private int randomRange(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}
