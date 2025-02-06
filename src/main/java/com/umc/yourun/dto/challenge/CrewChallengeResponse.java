package com.umc.yourun.dto.challenge;

import com.umc.yourun.domain.enums.ChallengeStatus;
import com.umc.yourun.domain.enums.Tendency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

public class CrewChallengeResponse {
    @Schema(description = "CHALLENGE_RES_00 : 참여자 성향 정보")
    public record MemberTendencyInfo(
            @Schema(description = "사용자 ID", example = "1")
            Long userId,

            @Schema(description = "크루원의 성향", example = "페이스메이커")
            Tendency memberTendency
    ) {}

    @Schema(title = "CREW_CHALLENGE_RES_01 : 크루 챌린지 생성 응답 DTO")
    public record CrewChallengeCreateRes (

            @Schema(description = "생성된 크루 챌린지 ID", example = "1")
            Long challengeId,

            @Schema(description = "크루명", example = "거진홍길동")
            String crewName,

            @Schema(description = "크루 구호", example = "헤르메스 신발의 주인공")
            String slogan,

            @Schema(description = "챌린지 시작일", example = "2025-01-14 15:00")
            String startDate,

            @Schema(description = "챌린지 마감일", example = "2025-01-16 15:00")
            String endDate,

            @Schema(description = "챌린지 기간", example = "3")
            int challengePeriod,

            @Schema(description = "유저 성향", example = "스프린터")
            Tendency tendency

    ) {}

    @Schema(title = "CREW_CHALLENGE_RES_02 : 4명 결성 대기 중인 크루 챌린지 조회 화면 응답 DTO")
    public record CrewChallenge (

            @Schema(description = "유저 ID", example = "1")
            Long userId,

            @Schema(description = "유저 성향", example = "페이스메이커")
            Tendency userTendency,

            @Schema(description = "유저의 크루 챌린지 보상 개수", example = "3")
            Long userCrewReward,

            @Schema(description = "유저의 솔로 챌린지 보상 개수", example = "3")
            Long userSoloReward,

            @Schema(description = "4명 결성 대기 중인 크루 챌린지들")
            List<CrewChallengeRes> crewChallengeResList
    ) {}

    @Schema(title = "CREW_CHALLENGE_RES_02 - 1: 4명 결성 대기 중인 크루 챌린지 응답 DTO")
    public record CrewChallengeRes(
            @Schema(description = "챌린지 ID", example = "1")
            Long challengeId,

            @Schema(description = "크루명", example = "거진홍길동")
            String crewName,

            @Schema(description = "챌린지 기간", example = "4")
            int challengePeriod,

            @Schema(description = "남은 인원", example = "1")
            int remaining,

            @Schema(description = "보상 개수", example = "2")
            int reward,

            @Schema(description = "참여자 ID 및 성향 목록")
            List<MemberTendencyInfo> participantIdsInfo

    ) {}

    @Schema(title = "CREW_CHALLENGE_RES_03 : 크루 챌린지 상세 페이지 응답 DTO")
    public record CrewChallengeDetailRes(

            @Schema(description = "크루명", example = "거진홍길동")
            String crewName,

            @Schema(description = "시작일", example = "2025-01-15 15:00")
            String startDate,

            @Schema(description = "마감일", example = "2025-01-20 15:00")
            String endDate,

            @Schema(description = "챌린지 기간", example = "4")
            int challengePeriod,

            @Schema(description = "참여 인원", example = "3")
            int joinCount,

            @Schema(description = "보상 개수", example = "2")
            int reward,

            @Schema(description = "크루원 ID 및 성향 목록")
            List<MemberTendencyInfo> participantIdsInfo,

            @Schema(description = "크루 구호", example = "헤르메스 신발의 주인공")
            String slogan
    ) {}

    @Schema(description = "CREW_CHALLENGE_RES_04 : 크루 챌린지 매칭 조회 응답 DTO")
    public record CrewChallengeMatchingRes(
            @Schema(description = "챌린지 기간(일)", example = "3")
            int period,

            @Schema(description = "내 크루명", example = "거진홍길동")
            String crewName,

            @Schema(description = "내 크루의 구호", example = "헤르메스 신발의 주인공")
            String myCrewSlogan,

            @Schema(description = "내 크루원 ID 및 성향 목록")
            List<MemberTendencyInfo> myParticipantIdsInfo,

            @Schema(description = "매칭된 크루명", example = "거진이봉주")
            String matchedCrewName,

            @Schema(description = "매칭된 크루의 구호", example = "에르메스 신발의 주인공")
            String matchedCrewSlogan,

            @Schema(description = "매칭된 크루원 ID 및 성향 목록")
            List<MemberTendencyInfo> matchedParticipantIdsInfo
    ) {}

    @Schema(description = "CREW_CHALLENGE_RES_05 : 크루 챌린지 상세 진행도 응답 DTO")
    public record CrewChallengeDetailProgressRes(
            @Schema(description = "설정된 기간", example = "3")
            int challengePeriod,

            @Schema(description = "내 크루명", example = "거진홍길동")
            String myCrewName,

            @Schema(description = "내 크루의 구호", example = "헤르메스 신발의 주인공")
            String myCrewSlogan,

            @Schema(description = "내 크루원 정보 목록(거리 및 성향)")
            List<CrewMemberInfo> myCrewMembers,

            @Schema(description = "내 크루 달린 거리", example = "20.5")
            double myCrewDistance,

            @Schema(description = "매칭된 크루명", example = "거진이봉주")
            String matchedCrewName,

            @Schema(description = "매칭된 크루의 구호", example = "에르메스 신발의 주인공")
            String matchedCrewSlogan,

            @Schema(description = "매칭된 크루 생성자 성향", example = "스프린터")
            Tendency matchedCrewCreatorTendency,

            @Schema(description = "매칭된 크루 달린 거리", example = "15.5")
            double matchedCrewDistance,

            @Schema(description = "현재시간", example = "2024/01/23 14:30")
            String now
    ) {}

    @Schema(description = "CREW_CHALLENGE_RES_05 - 1 : 크루원 정보")
    public record CrewMemberInfo(
            @Schema(description = "사용자 ID", example = "1")
            Long userId,

            @Schema(description = "달성한 거리(km)", example = "5.2")
            double runningDistance,

            @Schema(description = "크루원 성향", example = "스프린터")
            Tendency userTendency
    ) {}

    @Schema(description = "CREW_CHALLENGE_RES_06 : 러닝 후 크루 챌린지 결과 응답 DTO")
    public record CrewChallengeRunningResultRes(

            @Schema(description = "설정된 기간", example = "3")
            int challengePeriod,

            @Schema(description = "내 크루명", example = "거진홍길동")
            String myCrewName,

            @Schema(description = "유저가 뛰기 전 크루 총 거리", example = "12.8")
            double beforeDistance,

            @Schema(description = "유저가 방금 뛴 거리", example = "5.2")
            double userDistance,

            @Schema(description = "유저가 뛰어서 추가된 크루 총 거리", example = "18")
            double afterDistance,

            @Schema(description = "매칭된 크루 명", example = "거진이봉주")
            String matchedCrewName,

            @Schema(description = "매칭된 크루 생성자 성향", example = "스프린터")
            Tendency matchedCrewCreator,

            @Schema(description = "매칭된 크루 총 거리", example = "10")
            double matchedCrewDistance

    ) {}

    @Schema(title = "CREW_CHALLENGE_RES_07 : 크루 챌린지 순위 결과 조회 응답 DTO")
    public record CrewChallengeContributionRes (

            @Schema(description = "챌린지 기간", example = "3")
            int challengePeriod,

            @Schema(description = "챌린지 보상 개수", example = "1")
            int reward,

            @Schema(description = "크루명", example = "거진홍길동")
            String crewName,

            @Schema(description = "크루원 별 달린 거리 및 순위")
            List<CrewMemberRankingInfo> CrewMemberDistance,

            @Schema(description = "mvp 유저 ID", example = "1")
            Long mvpId,

            @Schema(description = "내 크루가 이겼는지", example = "true")
            boolean isWin
    ) {}

    @Schema(description = "CREW_CHALLENGE_RES_07 - 1 : 크루원 정보")
    public record CrewMemberRankingInfo(
            @Schema(description = "사용자 ID", example = "1")
            Long userId,

            @Schema(description = "달성한 거리(km)", example = "5.2")
            double runningDistance,

            @Schema(description = "크루원 성향", example = "스프린터")
            Tendency userTendency,

            @Schema(description = "달린 거리 별 순위", example = "1")
            int rank
    ) {}

    @Schema(description = "CREW_CHALLENGE_RES_08 : 크루 챌린지 참여 응답 DTO")
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
}