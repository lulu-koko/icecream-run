package com.icecreamrun.score;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "game_scores")
public class GameScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String userId;

    @Column(nullable = false)
    private boolean completed;

    @Column(nullable = false)
    private long durationMs;

    @Column(nullable = false)
    private int iceCreamCount;

    @Column(nullable = false)
    private int bananaHits;

    @Column(nullable = false)
    private int maxBrainFreeze;

    @Column(nullable = false)
    private int finalDistance;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected GameScore() {
    }

    public GameScore(String userId, boolean completed, long durationMs, int iceCreamCount,
            int bananaHits, int maxBrainFreeze, int finalDistance) {
        this.userId = userId;
        this.completed = completed;
        this.durationMs = durationMs;
        this.iceCreamCount = iceCreamCount;
        this.bananaHits = bananaHits;
        this.maxBrainFreeze = maxBrainFreeze;
        this.finalDistance = finalDistance;
    }

    @PrePersist
    void setCreatedAt() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public long getDurationMs() {
        return durationMs;
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

    public int getFinalDistance() {
        return finalDistance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
