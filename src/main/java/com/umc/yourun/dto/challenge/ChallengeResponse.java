package com.umc.yourun.dto.challenge;

import com.umc.yourun.domain.enums.ChallengeDistance;
import com.umc.yourun.domain.enums.ChallengePeriod;
import com.umc.yourun.domain.enums.ChallengeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public class ChallengeResponse {

    @Schema(title = "CHALLENGE_RES_01 : 4명 결성 대기 중인 크루 챌린지 응답 DTO")
    public record CrewChallengeRes(
            @Schema(description = "챌린지 ID", example = "1")
            Long challengeId,

            @Schema(description = "크루명", example = "거진홍길동")
            String crewName,

            @Schema(description = "시작일", example = "2025-01-15")
            LocalDate startDate,

            @Schema(description = "마감일", example = "2025-01-20")
            LocalDate endDate,

            @Schema(description = "챌린지 기간", example = "4")
            int challengePeriod,

            @Schema(description = "남은 인원", example = "1")
            int remaining,

            @Schema(description = "보상 개수", example = "2")
            int reward,

            @Schema(description = "참여자 ID 목록", example = """
                    [
                          1,
                          2,
                          3
                        ]""")
            List<Long> participantIds
    ) {}

    @Schema(title = "CHALLENGE_RES_02 : 매칭 대기 중인 솔로 챌린지 응답 DTO")
    public record SoloChallengeRes(
            @Schema(description = "챌린지 ID", example = "1")
            Long challengeId,

            @Schema(description = "시작일", example = "2025-01-15")
            LocalDate startDate,

            @Schema(description = "마감일", example = "2025-01-20")
            LocalDate endDate,

            @Schema(description = "챌린지 거리", example = "1")
            int challengeDistance,

            @Schema(description = "챌린지 기간", example = "4")
            int challengePeriod,

            @Schema(description = "챌린지 메이트 닉네임", example = "청정원")
            String challengeCreatorNickName,

            @Schema(description = "챌린지 메이트의 해시태그")
            List<String> challengeCreatorHashTags



    ) {}

    @Schema(title = "CHALLENGE_RES_03 : 솔로 챌린지 참여 응답 DTO")
    public record ChallengeMateRes(
            @Schema(description = "솔로 챌린지 ID", example = "1")
            Long challengeId,

            @Schema(description = "챌린지 생성자 ID", example = "2")
            Long userId
    ) {}

    @Schema(description = "CHALLENGE_RES_04 : 크루 챌린지 참여 응답 DTO")
    public record CrewChallengeMateRes(
            @Schema(description = "크루 챌린지 ID", example = "1")
            Long challengeId,

            @Schema(description = "참여자 ID 목록", example = """
                    [
                          1,
                          2,
                          3,
                          4
                        ]""")
            List<Long> participantIds
    ) {}

    @Schema(description = "CHALLENGE_RES_05 : 크루 챌린지 매칭 조회 응답 DTO")
    public record CrewMatchingRes(
            @Schema(description = "챌린지 기간(일)", example = "3")
            int period,

            @Schema(description = "내 크루명", example = "거진홍길동")
            String crewName,

            @Schema(description = "내 크루원 ID 목록 (참여 순서대로)", example = """
                    [
                          1,
                          2,
                          3,
                          4
                        ]""")
            List<Long> crewMemberIds,

            @Schema(description = "매칭된 크루명", example = "거진이봉주")
            String matchedCrewName,

            @Schema(description = "매칭된 크루원 ID 목록 (참여 순서대로)", example = """
                    [
                          5,
                          6,
                          7,
                          8
                        ]""")
            List<Long> matchedCrewMemberIds
    ) {}

    @Schema(description = "CHALLENGE_RES_06 : 홈 화면 챌린지 조회 응답 DTO")
    public record HomeChallengeRes(
            @Schema(description = "솔로 챌린지 정보")
            UserSoloChallengeInfo soloChallenge,

            @Schema(description = "크루 챌린지 정보")
            UserCrewChallengeInfo crewChallenge
    ) {}

    @Schema(description = "CHALLENGE_RES_06 - 1 : 유저의 솔로 챌린지 응답 DTO")
    public record UserSoloChallengeInfo(
            @Schema(description = "챌린지 ID")
            Long challengeId,

            @Schema(description = "챌린지 상태", example = "PENDING/IN_PROGRESS")
            ChallengeStatus status,

            @Schema(description = "목표 거리", example = "1")
            int challengeDistance,

            @Schema(description = "챌린지 기간(일)", example = "3")
            int challengePeriod,

            @Schema(description = "챌린지 메이트 ID", example = "1")
            Long challengeMateId,

            @Schema(description = "챌린지 메이트 닉네임", example = "청정원")
            String challengeMateNickName,

            @Schema(description = "솔로 챌린지 진행 일차", example = "3")
            int soloDayCount,

            @Schema(description = "솔로 챌린지 시작일", example = "2025-01-22")
            LocalDate soloStartDate
    ) {}

    @Schema(description = "CHALLENGE_RES_06 - 2 : 유저의 크루 챌린지 응답 DTO")
    public record UserCrewChallengeInfo(
            @Schema(description = "챌린지 ID", example = "1")
            Long challengeId,

            @Schema(description = "크루명", example = "거진홍길동")
            String crewName,

            @Schema(description = "챌린지 상태", example = "PENDING/IN_PROGRESS")
            ChallengeStatus challengeStatus,

            @Schema(description = "챌린지 기간(일)", example = "3")
            int challengePeriod,

            @Schema(description = "크루원 ID 목록", example = """
                    [
                          1,
                          2,
                          3,
                          4
                        ]""")
            List<Long> crewMemberIds,

            @Schema(description = "크루 챌린지 진행 일차", example = "2")
            int crewDayCount,

            @Schema(description = "크루 챌린지 시작일", example = "2025-01-22")
            LocalDate crewStartDate
    ) {}

    @Schema(description = "CHALLENGE_RES_07 : 팀원별 거리 포함 크루 챌린지 진행도 DTO")
    public record CrewChallengeDetailRes(
            @Schema(description = "설정된 기간", example = "3")
            int challengePeriod,

            @Schema(description = "내 크루명", example = "달리기조")
            String myCrewName,

            @Schema(description = "내 크루원 정보 목록")
            List<CrewMemberInfo> myCrewMembers,

            @Schema(description = "매칭된 크루명")
            String crewName,

            @Schema(description = "매칭된 크루의 크루원들 ID 목록", example = """
                    [
                          1,
                          2,
                          3,
                          4
                        ]""")
            List<Long> matchedCrewMemberIds,

            @Schema(description = "전체 거리 대비 우리 크루 달성 비율(%)", example = "55.5")
            double progressRatio
    ) {}

    @Schema(description = "크루원 정보")
    public record CrewMemberInfo(
            @Schema(description = "사용자 ID", example = "1")
            Long userId,

            @Schema(description = "달성한 거리(m)", example = "3000")
            int runningDistance
    ) {}
}