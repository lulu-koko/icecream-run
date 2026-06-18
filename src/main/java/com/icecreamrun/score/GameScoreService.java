package com.icecreamrun.score;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameScoreService {
    private final GameScoreRepository repository;

    public GameScoreService(GameScoreRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public GameScoreResponse save(GameScoreRequest request) {
        GameScore score = new GameScore(
                request.userId().trim(),
                request.completed(),
                request.durationMs(),
                request.iceCreamCount(),
                request.bananaHits(),
                request.maxBrainFreeze(),
                request.finalDistance()
        );
        return GameScoreResponse.from(repository.save(score));
    }

    @Transactional(readOnly = true)
    public List<GameScoreResponse> leaderboard(int requestedLimit) {
        int limit = Math.max(1, Math.min(requestedLimit, 1000));
        return repository.findLeaderboard(PageRequest.of(0, limit))
                .stream()
                .map(GameScoreResponse::from)
                .toList();
    }
}
