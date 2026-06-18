package com.icecreamrun.data;

public class GameResult {
    private final GameResultType type;
    private final FailReason failReason;
    private final int distanceMeters;
    private final int elapsedSeconds;
    private final int iceCreamCount;
    private final int bananaHits;
    private final int maxBrainFreeze;

    public GameResult(GameResultType type, FailReason failReason, int distanceMeters,
            int elapsedSeconds, int iceCreamCount, int bananaHits, int maxBrainFreeze) {
        this.type = type;
        this.failReason = failReason;
        this.distanceMeters = distanceMeters;
        this.elapsedSeconds = elapsedSeconds;
        this.iceCreamCount = iceCreamCount;
        this.bananaHits = bananaHits;
        this.maxBrainFreeze = maxBrainFreeze;
    }

    public GameResultType getType() {
        return type;
    }

    public FailReason getFailReason() {
        return failReason;
    }

    public int getDistanceMeters() {
        return distanceMeters;
    }

    public int getElapsedSeconds() {
        return elapsedSeconds;
    }

    public int getIceCreamCount() {
        return iceCreamCount;
    }

    public int getBananaHits() {
        return bananaHits;
    }

    public int getMaxBrainFreeze() {
        return maxBrainFreeze;
    }
}
