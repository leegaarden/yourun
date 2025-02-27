package com.umc.yourun.converter;

import com.umc.yourun.domain.CrewChallenge;
import com.umc.yourun.domain.SoloChallenge;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.ChallengePeriod;
import com.umc.yourun.domain.enums.Tendency;
import com.umc.yourun.domain.mapping.UserCrewChallenge;
import com.umc.yourun.domain.mapping.UserSoloChallenge;
import com.umc.yourun.dto.challenge.ChallengeRequest;
import com.umc.yourun.dto.challenge.CrewChallengeResponse;
import com.umc.yourun.dto.challenge.SoloChallengeResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ChallengeConverter {
    //  1. DTO -> Entity

    // 1-1. 크루 챌린지 생성
    public static CrewChallenge toCrewChallenge(ChallengeRequest.CreateCrewChallengeReq request,
                                                LocalDateTime startTime, LocalDateTime endTime,
                                                ChallengePeriod challengePeriod) {
        return CrewChallenge.builder()
                .crewName(request.crewName())
                .startDate(startTime)
                .endDate(endTime)
                .slogan(request.slogan())
                .challengePeriod(challengePeriod)
                .build();
    }

    // 1-2. 솔로 챌린지 생성
    public static SoloChallenge toSoloChallenge(ChallengeRequest.CreateSoloChallengeReq request,
                                                LocalDateTime startTime, LocalDateTime endTime,
                                                ChallengePeriod challengePeriod) {
        return SoloChallenge.builder()
                .endDate(endTime)
                .startDate(startTime)
                .challengeDistance(request.challengeDistance())
                .challengePeriod(challengePeriod)
                .build();
    }

    // 1-3. 유저 솔로 챌린지 생성
    public static UserSoloChallenge toUserSoloChallenge(User user, SoloChallenge challenge, boolean isCreator) {
        return UserSoloChallenge.builder()
                .user(user)
                .soloChallenge(challenge)
                .isCreator(isCreator)
                .build();
    }

    // 1-4. 유저 크루 챌린지로
    public static UserCrewChallenge toUserCrewChallenge(User user, CrewChallenge challenge, boolean isCreator) {
        return UserCrewChallenge.builder()
                .user(user)
                .crewChallenge(challenge)
                .isCreator(isCreator)
                .build();
    }

    // 2. Entity -> DTO

    // 2-1. 사용자 관련된 솔로 챌린지 정보 응답
    public static SoloChallengeResponse.UserSoloChallengeInfo toUserSoloChallengeInfo(
            SoloChallenge challenge,
            User user,
            Long challengeMateId,
            String challengeMateNickName,
            Tendency challengeMateTendency,
            int soloCountDay,
            String startDate) {
        return SoloChallengeResponse.UserSoloChallengeInfo.builder()
                .challengeDistance(challenge.getChallengeDistance().getDistance())
                .challengeId(challenge.getId())
                .status(challenge.getChallengeStatus())
                .challengePeriod(challenge.getChallengePeriod().getDays())
                .challengeMateId(challengeMateId)
                .challengeMateNickName(challengeMateNickName)
                .challengeMateTendency(challengeMateTendency)
                .userId(user.getId())
                .userNickName(user.getNickname())
                .userTendency(user.getTendency())
                .soloDayCount(soloCountDay)
                .soloStartDate(startDate)
                .build();

    }

//    // 2-2. 사용자 관련 크루 챌린지 정보 응답
    public static SoloChallengeResponse.UserCrewChallengeInfo toUserCrewChallengeInfo(
            CrewChallenge challenge,
            List<CrewChallengeResponse.MemberTendencyInfo> myParticipantIdsInfo,
            int crewCountDay,
            String startDate) {
        return SoloChallengeResponse.UserCrewChallengeInfo.builder()
                .challengeId(challenge.getId())
                .crewName(challenge.getCrewName())
                .challengeStatus(challenge.getChallengeStatus())
                .challengePeriod(challenge.getChallengePeriod().getDays())
                .myParticipantIdsInfo(myParticipantIdsInfo)
                .crewDayCount(crewCountDay)
                .crewStartDate(startDate)
                .build();
    }

}