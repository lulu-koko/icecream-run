package com.icecreamrun.score;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GameScoreRequest(
        @NotBlank @Size(max = 40) String userId,
        boolean completed,
        @Min(1) @Max(3_600_000) long durationMs,
        @Min(0) @Max(999) int iceCreamCount,
        @Min(0) @Max(3) int bananaHits,
        @Min(0) @Max(100) int maxBrainFreeze,
        @Min(0) @Max(1000) int finalDistance
) {
}
