package com.umc.yourun.dto.challenge;

import com.umc.yourun.domain.enums.ChallengeDistance;
import com.umc.yourun.domain.enums.ChallengePeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public class ChallengeResponse {

    @Schema(title = "CHALLENGE_RES_01 : 대기 중인 크루 챌린지 응답 DTO")
    public record CrewChallengePendingRes(
            @Schema(description = "챌린지 ID", example = "1")
            Long challengeId,

            @Schema(description = "크루명", example = "거진홍길동")
            String crewName,

            @Schema(description = "시작일", example = "2025-01-15")
            LocalDate startDate,

            @Schema(description = "종료일", example = "2025-01-20")
            LocalDate endDate,

            @Schema(description = "챌린지 기간", example = "FOUR")
            ChallengePeriod challengePeriod
    ) {}
}