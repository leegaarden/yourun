package com.umc.yourun.dto.challenge;

import com.umc.yourun.domain.enums.ChallengeDistance;
import com.umc.yourun.domain.enums.ChallengePeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public class ChallengeResponse {

    // TODO: 남은 인원 응답에 추가하기
    @Schema(title = "CHALLENGE_RES_01 : 상태 별 크루 챌린지 응답 DTO")
    public record CrewChallengeStatusRes(
            @Schema(description = "챌린지 ID", example = "1")
            Long challengeId,

            @Schema(description = "크루명", example = "거진홍길동")
            String crewName,

            @Schema(description = "시작일", example = "2025-01-15")
            LocalDate startDate,

            @Schema(description = "마감일", example = "2025-01-20")
            LocalDate endDate,

            @Schema(description = "챌린지 기간", example = "FOUR")
            ChallengePeriod challengePeriod
    ) {}

    // TODO: 만든 사용자의 해시태그 응답에 추가
    @Schema(title = "CHALLENGE_RES_02 : 상태 별 솔로 챌린지 응답 DTO")
    public record SoloChallengeStatusRes(
            @Schema(description = "챌린지 ID", example = "1")
            Long challengeId,

            @Schema(description = "시작일", example = "2025-01-15")
            LocalDate startDate,

            @Schema(description = "마감일", example = "2025-01-20")
            LocalDate endDate,

            @Schema(description = "챌린지 거리", example = "ONE_KM",
                    title = "ONE_KM = 1, THREE_KM = 3, FIVE_KM = 5")
            ChallengeDistance challengeDistance,

            @Schema(description = "챌린지 기간", example = "FOUR")
            ChallengePeriod challengePeriod
    ) {}

    @Schema(title = "CHALLENGE_RES_03 : 솔로 챌린지 참여 응답 DTO")
    public record ChallengeMateRes(
            @Schema(description = "챌린지 ID", example = "1")
            Long challengeId,

            @Schema(description = "챌린지 생성자 ID", example = "null")
            Long userId
    ) {}
}