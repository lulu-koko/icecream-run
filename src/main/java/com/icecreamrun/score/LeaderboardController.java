package com.icecreamrun.score;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LeaderboardController {
    private final GameScoreService service;

    public LeaderboardController(GameScoreService service) {
        this.service = service;
    }

    @PostMapping("/scores")
    public GameScoreResponse submitScore(@Valid @RequestBody GameScoreRequest request) {
        return service.save(request);
    }

    @GetMapping("/leaderboard")
    public List<GameScoreResponse> leaderboard(@RequestParam(defaultValue = "20") int limit) {
        return service.leaderboard(limit);
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }
}
