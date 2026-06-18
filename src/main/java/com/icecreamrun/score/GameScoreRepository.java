package com.icecreamrun.score;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GameScoreRepository extends JpaRepository<GameScore, Long> {
    @Query("""
            select score
            from GameScore score
            order by score.completed desc,
                     score.durationMs asc,
                     score.iceCreamCount desc,
                     score.createdAt desc
            """)
    List<GameScore> findLeaderboard(Pageable pageable);
}
