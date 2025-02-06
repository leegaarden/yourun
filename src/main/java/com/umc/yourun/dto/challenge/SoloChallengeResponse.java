package com.umc.yourun.dto.challenge;

import com.umc.yourun.domain.enums.ChallengeStatus;
import com.umc.yourun.domain.enums.Tendency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

public class SoloChallengeResponse {

    @Schema(title = "SOLO_CHALLENGE_RES_01 : 솔로 챌린지 생성 응답 DTO")
    public record SoloChallengeCreateRes (

            @Schema(description = "생성된 솔로 챌린지 ID", example = "1")
            Long challengeId,

            @Schema(description = "챌린지 시작일", example = "2025-01-14 15:00")
            String startDate,

            @Schema(description = "챌린지 마감일", example = "2025-01-16 15:00")
            String endDate,

            @Schema(description = "챌린지 기간", example = "3")
            int challengePeriod,

            @Schema(description = "유저 성향", example = "스프린터")
            Tendency tendency

    ) {}

    @Schema(title = "SOLO_CHALLENGE_RES_02 : 매칭 대기 중인 솔로 챌린지 조회 화면 응답 DTO")
    public record SoloChallengeRes(

            @Schema(description = "유저 ID", example = "1")
            Long userId,

            @Schema(description = "유저 성향", example = "페이스메이커")
            Tendency userTendency,

            @Schema(description = "유저의 크루 챌린지 보상 개수", example = "3")
            Long userCrewReward,

            @Schema(description = "유저의 솔로 챌린지 보상 개수", example = "3")
            Long userSoloReward,

            @Schema(description = "매칭 대기 중인 솔로 챌린지들")
            List<SoloChallenge> soloChallengeList
    ) {}

    @Schema(title = "SOLO_CHALLENGE_RES_02 - 1 : 매칭 대기 중인 솔로 챌린지 응답 DTO")
    public record SoloChallenge(
            @Schema(description = "챌린지 ID", example = "1")
            Long challengeId,

            @Schema(description = "챌린지 거리", example = "1")
            int challengeDistance,

            @Schema(description = "챌린지 기간", example = "4")
            int challengePeriod,

            @Schema(description = "챌린지 생성자닉네임", example = "청정원")
            String challengeCreatorNickName,

            @Schema(description = "챌린지 생성자의 해시태그")
            List<String> challengeCreatorHashTags,

            @Schema(description = "보상 개수", example = "2")
            int reward,

            @Schema(description = "챌린지 생성자의 성향", example = "스프린터")
            Tendency challengeCreatorTendency

    ) {}

    @Schema(title = "SOLO_CHALLENGE_RES_03 : 솔로 챌린지 상세 페이지 응답 DTO")
    public record SoloChallengeDetailRes(

            @Schema(description = "시작일", example = "2025-01-15 15:00")
            String startDate,

            @Schema(description = "마감일", example = "2025-01-20 15:00")
            String endDate,

            @Schema(description = "챌린지 거리", example = "1")
            int challengeDistance,

            @Schema(description = "챌린지 기간", example = "4")
            int challengePeriod,

            @Schema(description = "챌린지 생성자 닉네임", example = "청정원")
            String challengeCreatorNickName,

            @Schema(description = "챌린지 생성자 해시태그")
            List<String> challengeCreatorHashTags,

            @Schema(description = "챌린지 생성자 성향", example = "트레일러너")
            Tendency tendency,

            @Schema(description = "보상 개수", example = "2")
            int reward,

            @Schema(description = "챌린지 메이트의 앱 사용 기간", example = "27")
            int countDay

    ) {}

    @Schema(description = "SOLO_CHALLENGE_RES_04 : 솔로 챌린지 매칭 조회 응답 DTO")
    public record SoloChallengeMatchingRes(
            @Schema(description = "챌린지 기간(일)", example = "3")
            int period,

            @Schema(description = "챌린지 거리", example = "1")
            int challengeDistance,

            @Schema(description = "유저 성향", example = "페이스메이커")
            Tendency userTendency,

            @Schema(description = "유저 닉네임", example = "청정원")
            String userNickName,

            @Schema(description = "유저 앱 사용 기간", example = "27")
            int userCountDay,

            @Schema(description = "유저 해시태그")
            List<String> userHashTags,

            @Schema(description = "챌린지 메이트 성향", example = "페이스메이커")
            Tendency challengeMateTendency,

            @Schema(description = "챌린지 메이트 닉네임", example = "루시")
            String challengeMateNickName,

            @Schema(description = "챌린지 메이트 앱 사용 기간", example = "27")
            int challengeMateCountDay,

            @Schema(description = "챌린지 메이트 해시태그")
            List<String> challengeMateHashTags

    ) {}

    @Schema(description = "SOLO_CHALLENGE_RES_05 : 일자별 솔로 챌린지 진행도 응답 DTO")
    public record SoloChallengeProgressRes(

            @Schema(description = "챌린지 기간", example = "3")
            int challengePeriod,

            @Schema(description = "챌린지 거리", example = "5")
            int challengeDistance,

            @Schema(description = "챌린지 진행 며칠 째인지", example = "2")
            int dayCount,

            @Schema(description = "현재 날짜 및 시간", example = "2025-02-04 15:00")
            String now,

            @Schema(description = "일자별 챌린지 결과")
            List<DayResult> dayResultInfos,

            @Schema(description = "챌린지 메이트 정보")
            ChallengeMateInfo challengeMateInfo,

            @Schema(description = "유저의 금일 성공 여부")
            boolean userIsSuccess,

            @Schema(description = "유저 성향", example = "스프린터")
            Tendency userTendency

    ) {}

    @Schema(description = "SOLO_CHALLENGE_RES_05 -1 : 일자별 달린 거리")
    public record DayResult (

            @Schema(description = "일자", example = "1")
            int day,

            @Schema(description = "일자의 달린 거리", example = "5.2")
            double distance,

            @Schema(description = "핻당 일자의 성공 여부", example = "true")
            boolean isSuccess
    ) {}

    @Schema(description = "SOLO_CHALLENGE_RES_05 -2 : 챌린지 메이트 달린 거리 및 정보")
    public record ChallengeMateInfo (

            @Schema(description = "챌린지 메이트 닉네임", example = "하리보")
            String challengeMateNickName,

            @Schema(description = "챌린지 메이트 성향", example = "페이스메이커")
            Tendency challengeMateTendency,

            @Schema(description = "며칠 동안 성공했는지 3/5의 3", example = "3")
            int successDay,

            @Schema(description = "성공 여부", example = "false")
            boolean challengeMateIsSuccess,

            @Schema(description = "챌린지 메이트가 뛴 거리", example = "2.5")
            double distance
    ) {}

    @Schema(description = "SOLO_CHALLENGE_RES_06 : 러닝 후 솔로 챌린지 결과 조회 응답 DTO")
    public record SoloChallengeRunningResultRes (

            @Schema(description = "챌린지 기간", example = "3")
            int challengePeriod,

            @Schema(description = "챌린지 거리", example = "5")
            int challengeDistance,

            @Schema(description = "챌린지 진행 며칠 째인지", example = "2")
            int dayCount,

            @Schema(description = "챌린지 메이트 정보")
            ChallengeMateInfo challengeMateInfo,

            @Schema(description = "유저의 성공 여부")
            boolean userIsSuccess,

            @Schema(description = "유저 성향", example = "스프린터")
            Tendency userTendency

    ) {}

    @Schema(title = "SOLO_CHALLENGE_RES_07 : 솔로 챌린지 참여 응답 DTO")
    public record SoloChallengeMateRes(
            @Schema(description = "솔로 챌린지 ID", example = "1")
            Long challengeId,

            @Schema(description = "챌린지 생성자 ID", example = "2")
            Long userId
    ) {}

    @Schema(description = "CHALLENGE_RES_01 : 홈 화면 챌린지 조회 응답 DTO")
    public record HomeChallengeRes(
            @Schema(description = "솔로 챌린지 정보")
            UserSoloChallengeInfo soloChallenge,

            @Schema(description = "크루 챌린지 정보")
            UserCrewChallengeInfo crewChallenge
    ) {}

    @Builder
    @Schema(description = "CHALLENGE_RES_01 - 1 : 유저의 솔로 챌린지 응답 DTO")
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

            @Schema(description = "챌린지 메이트의 성향", example = "스프린터")
            Tendency challengeMateTendency,

            @Schema(description = "사용자 ID", example = "1")
            Long userId,

            @Schema(description = "사용자 닉네임", example = "청정원")
            String userNickName,

            @Schema(description = "사용자의 성향", example = "스프린터")
            Tendency userTendency,

            @Schema(description = "솔로 챌린지 진행 일차", example = "3")
            int soloDayCount,

            @Schema(description = "솔로 챌린지 시작일", example = "2025-01-22 15:00")
            String soloStartDate
    ) {}

    @Builder
    @Schema(description = "CHALLENGE_RES_01 - 2 : 유저의 크루 챌린지 응답 DTO")
    public record UserCrewChallengeInfo(
            @Schema(description = "챌린지 ID", example = "1")
            Long challengeId,

            @Schema(description = "크루명", example = "거진홍길동")
            String crewName,

            @Schema(description = "챌린지 상태", example = "PENDING/IN_PROGRESS")
            ChallengeStatus challengeStatus,

            @Schema(description = "챌린지 기간(일)", example = "3")
            int challengePeriod,

            @Schema(description = "내 크루원 ID 및 성향 목록")
            List<CrewChallengeResponse.MemberTendencyInfo> myParticipantIdsInfo,

            @Schema(description = "크루 챌린지 진행 일차", example = "2")
            int crewDayCount,

            @Schema(description = "크루 챌린지 시작일", example = "2025-01-22 15:00")
            String crewStartDate
    ) {}
}
