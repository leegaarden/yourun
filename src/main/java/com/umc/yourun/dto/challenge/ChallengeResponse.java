package com.umc.yourun.dto.challenge;

import com.umc.yourun.domain.enums.ChallengeStatus;
import com.umc.yourun.domain.enums.Tendency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;

// TODO : 성향 받아야 함
public class ChallengeResponse {

    @Schema(description = "CHALLENGE_RES_00 : 참여자 성향 정보")
    public record MemberTendencyInfo(
            @Schema(description = "사용자 ID", example = "1")
            Long userId,

            @Schema(description = "크루원의 성향", example = "페이스메이커")
            Tendency memberTendency
    ) {}

    @Schema(title = "CHALLENGE_RES_01 : 4명 결성 대기 중인 크루 챌린지 조회 화면 응답 DTO")
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

    @Schema(title = "CHALLENGE_RES_01 - 1: 4명 결성 대기 중인 크루 챌린지 응답 DTO")
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

    @Schema(title = "CHALLENGE_RES_02 : 매칭 대기 중인 솔로 챌린지 조회 화면 응답 DTO")
    public record SoloChallenge (

            @Schema(description = "유저 ID", example = "1")
            Long userId,

            @Schema(description = "유저 성향", example = "페이스메이커")
            Tendency userTendency,

            @Schema(description = "유저의 크루 챌린지 보상 개수", example = "3")
            Long userCrewReward,

            @Schema(description = "유저의 솔로 챌린지 보상 개수", example = "3")
            Long userSoloReward,

            @Schema(description = "매칭 대기 중인 솔로 챌린지들")
            List<SoloChallengeRes> soloChallengeResList
    ) {}

    @Schema(title = "CHALLENGE_RES_02 - 1 : 매칭 대기 중인 솔로 챌린지 응답 DTO")
    public record SoloChallengeRes(
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

    @Schema(title = "CHALLENGE_RES_03 : 솔로 챌린지 참여 응답 DTO")
    public record SoloChallengeMateRes(
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

    @Schema(description = "CHALLENGE_RES_06 : 홈 화면 챌린지 조회 응답 DTO")
    public record HomeChallengeRes(
            @Schema(description = "솔로 챌린지 정보")
            UserSoloChallengeInfo soloChallenge,

            @Schema(description = "크루 챌린지 정보")
            UserCrewChallengeInfo crewChallenge
    ) {}

    @Builder
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

            @Schema(description = "솔로 챌린지 시작일", example = "2025-01-22")
            String soloStartDate
    ) {}

    @Builder
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

            @Schema(description = "내 크루원 ID 및 성향 목록")
            List<MemberTendencyInfo> myParticipantIdsInfo,

            @Schema(description = "크루 챌린지 진행 일차", example = "2")
            int crewDayCount,

            @Schema(description = "크루 챌린지 시작일", example = "2025-01-22")
            String crewStartDate
    ) {}

    @Schema(description = "CHALLENGE_RES_07 : 크루 챌린지 상세 진행도 응답 DTO")
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
            String now,

            @Schema(description = "유저 크루가 이기고 있는지", example = "true")
            boolean win
    ) {}

    @Schema(description = "CHALLENGE_RES_07 - 1 : 크루원 정보")
    public record CrewMemberInfo(
            @Schema(description = "사용자 ID", example = "1")
            Long userId,

            @Schema(description = "달성한 거리(km)", example = "5.2")
            double runningDistance,

            @Schema(description = "크루원 성향", example = "스프린터")
            Tendency userTendency
    ) {}

    @Schema(title = "CHALLENGE_RES_08 : 크루 챌린지 상세 페이지 응답 DTO")
    public record CrewChallengeDetailRes(

            @Schema(description = "크루명", example = "거진홍길동")
            String crewName,

            @Schema(description = "시작일", example = "2025-01-15")
            String startDate,

            @Schema(description = "마감일", example = "2025-01-20")
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

    @Schema(title = "CHALLENGE_RES_09 : 솔로 챌린지 상세 페이지 응답 DTO")
    public record SoloChallengeDetailRes(

            @Schema(description = "시작일", example = "2025-01-15")
            String startDate,

            @Schema(description = "마감일", example = "2025-01-20")
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

    @Schema(title = "CHALLENGE_RES_10 : 크루 챌린지 생성 응답 DTO")
    public record CrewChallengeCreate (

            @Schema(description = "생성된 크루 챌린지 ID", example = "1")
            Long challengeId,

            @Schema(description = "크루명", example = "거진홍길동")
            String crewName,

            @Schema(description = "크루 구호", example = "헤르메스 신발의 주인공")
            String slogan,

            @Schema(description = "챌린지 시작일", example = "2025-01-14")
            String startDate,

            @Schema(description = "챌린지 마감일", example = "2025-01-16")
            String endDate,

            @Schema(description = "챌린지 기간", example = "3")
            int challengePeriod,

            @Schema(description = "유저 성향", example = "스프린터")
            Tendency tendency

    ) {}

    @Schema(title = "CHALLENGE_RES_11 : 솔로 챌린지 생성 응답 DTO")
    public record SoloChallengeCreate (

            @Schema(description = "생성된 솔로 챌린지 ID", example = "1")
            Long challengeId,

            @Schema(description = "챌린지 시작일", example = "2025-01-14")
            String startDate,

            @Schema(description = "챌린지 마감일", example = "2025-01-16")
            String endDate,

            @Schema(description = "챌린지 기간", example = "3")
            int challengePeriod,

            @Schema(description = "유저 성향", example = "스프린터")
            Tendency tendency

    ) {}

    @Schema(description = "CHALLENGE_RES_12 : 솔로 챌린지 매칭 조회 응답 DTO")
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

    @Schema(title = "CHALLENGE_RES_13 : 러닝 후 크루 챌린지 순위 결과 조회 응답 DTO")
    public record CrewChallengeContributionRes (

            @Schema(description = "챌린지 기간", example = "3")
            int challengePeriod,

            @Schema(description = "챌린지 보상 개수", example = "1")
            int reward,

            @Schema(description = "크루명", example = "거진홍길동")
            String crewName,

            @Schema(description = "크루원 별 달린 거리")
            List<CrewMemberInfo> CrewMemberDistance,

            @Schema(description = "mvp 유저 ID", example = "1")
            Long mvpId,

            @Schema(description = "내 크루가 이겼는지", example = "true")
            boolean win
    ) {}

}