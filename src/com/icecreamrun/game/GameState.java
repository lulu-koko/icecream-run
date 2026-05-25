package com.icecreamrun.game;

import com.icecreamrun.model.BananaPeel;
import com.icecreamrun.model.IceCreamItem;
import com.icecreamrun.model.Player;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private double distanceMeters;
    private double brainFreeze;
    private int maxBrainFreeze;
    private int iceCreamCount;
    private int bananaHits;
    private int elapsedSeconds;
    private long boostEndTimeMillis;
    private Player player;
    private List<BananaPeel> bananaPeels;
    private List<IceCreamItem> iceCreamItems;

    public GameState() {
        reset();
    }

    public void reset() {
        distanceMeters = 0;
        brainFreeze = 0;
        maxBrainFreeze = 0;
        iceCreamCount = 0;
        bananaHits = 0;
        elapsedSeconds = 0;
        boostEndTimeMillis = 0;
        player = new Player(120, 0, GameConfig.PLAYER_WIDTH, GameConfig.PLAYER_HEIGHT);
        bananaPeels = new ArrayList<>();
        iceCreamItems = new ArrayList<>();
    }

    public void addDistance(double meters) {
        distanceMeters = Math.min(GameConfig.TARGET_DISTANCE_METERS, distanceMeters + meters);
    }

    public void setElapsedSeconds(int elapsedSeconds) {
        this.elapsedSeconds = elapsedSeconds;
    }

    public void addBananaHit() {
        bananaHits++;
    }

    public void eatIceCream(long nowMillis) {
        iceCreamCount++;
        brainFreeze = Math.min(GameConfig.MAX_BRAIN_FREEZE,
                brainFreeze + GameConfig.ICE_CREAM_BRAIN_FREEZE);
        maxBrainFreeze = Math.max(maxBrainFreeze, getBrainFreeze());
        boostEndTimeMillis = nowMillis + GameConfig.BOOST_DURATION_MS;
    }

    public void decayBrainFreeze(double deltaSeconds) {
        brainFreeze = Math.max(0, brainFreeze - GameConfig.BRAIN_FREEZE_DECAY_PER_SECOND * deltaSeconds);
    }

    public boolean isBoostActive(long nowMillis) {
        return nowMillis < boostEndTimeMillis;
    }

    public int getDistanceMeters() {
        return (int) Math.floor(distanceMeters);
    }

    public int getBrainFreeze() {
        return (int) Math.round(brainFreeze);
    }

    public int getMaxBrainFreeze() {
        return maxBrainFreeze;
    }

    public int getIceCreamCount() {
        return iceCreamCount;
    }

    public int getBananaHits() {
        return bananaHits;
    }

    public int getElapsedSeconds() {
        return elapsedSeconds;
    }

    public Player getPlayer() {
        return player;
    }

    public List<BananaPeel> getBananaPeels() {
        return bananaPeels;
    }

    public List<IceCreamItem> getIceCreamItems() {
        return iceCreamItems;
    }
}
