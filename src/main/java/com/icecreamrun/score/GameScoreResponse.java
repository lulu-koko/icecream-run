package com.icecreamrun.score;

import java.time.Instant;

public record GameScoreResponse(
        Long id,
        String userId,
        boolean completed,
        long durationMs,
        int iceCreamCount,
        int bananaHits,
        int maxBrainFreeze,
        int finalDistance,
        Instant createdAt
) {
    public static GameScoreResponse from(GameScore score) {
        return new GameScoreResponse(
                score.getId(),
                score.getUserId(),
                score.isCompleted(),
                score.getDurationMs(),
                score.getIceCreamCount(),
                score.getBananaHits(),
                score.getMaxBrainFreeze(),
                score.getFinalDistance(),
                score.getCreatedAt()
        );
    }
}
