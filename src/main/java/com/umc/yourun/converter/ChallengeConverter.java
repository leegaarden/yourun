package com.umc.yourun.converter;

import com.umc.yourun.domain.CrewChallenge;
import com.umc.yourun.domain.SoloChallenge;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.ChallengePeriod;
import com.umc.yourun.domain.enums.ChallengeStatus;
import com.umc.yourun.domain.mapping.UserCrewChallenge;
import com.umc.yourun.domain.mapping.UserSoloChallenge;
import com.umc.yourun.dto.challenge.ChallengeRequest;
import com.umc.yourun.dto.challenge.ChallengeResponse;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ChallengeConverter {
    //  1. DTO -> Entity

    // 1-1. 크루 챌린지 생성
    public static CrewChallenge toCrewChallenge(ChallengeRequest.CreateCrewChallengeReq request, ChallengePeriod challengePeriod) {
        return CrewChallenge.builder()
                .crewName(request.crewName())
                .endDate(request.endDate())
                .slogan(request.slogan())
                .challengePeriod(challengePeriod)
                .build();
    }

    // 1-2. 솔로 챌린지 생성
    public static SoloChallenge toSoloChallenge(ChallengeRequest.CreateSoloChallengeReq request, ChallengePeriod challengePeriod) {
        return SoloChallenge.builder()
                .endDate(request.endDate())
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

    // 2-1. 상태별 크루 챌린지 응답
    public static ChallengeResponse.CrewChallengeStatusRes toStatusCrewChallengeRes (CrewChallenge challenge) {
        return new ChallengeResponse.CrewChallengeStatusRes(
                challenge.getId(),
                challenge.getCrewName(),
                challenge.getStartDate(),
                challenge.getEndDate(),
                challenge.getChallengePeriod().getDays()
        );
    }

    // 2-2. 상탭별 솔로 챌린지 응답
    public static ChallengeResponse.SoloChallengeStatusRes toStatusSoloChallengeRes (SoloChallenge challenge) {
        return new ChallengeResponse.SoloChallengeStatusRes(
                challenge.getId(),
                challenge.getStartDate(),
                challenge.getEndDate(),
                challenge.getChallengeDistance().getDistance(),
                challenge.getChallengePeriod().getDays()
        );
    }

    // 2-3. 사용자 관련된 솔로 챌린지 정보 응답
    public static ChallengeResponse.UserSoloChallengeInfo toUserSoloChallengeInfo(
            SoloChallenge challenge,
            Long mateId,
            int soloCountDay,
            String mateNickName) {
        return new ChallengeResponse.UserSoloChallengeInfo(
                challenge.getId(),
                challenge.getChallengeStatus(),
                challenge.getChallengeDistance().getDistance(),
                challenge.getChallengePeriod().getDays(),
                mateId,
                mateNickName,
                soloCountDay,
                challenge.getStartDate()
        );
    }

    // 2-4. 사용자 관련 크루 챌린지 정보 응답
    public static ChallengeResponse.UserCrewChallengeInfo toUserCrewChallengeInfo(
            CrewChallenge challenge,
            List<Long> crewMemberIds,
            int crewCountDay) {
        return new ChallengeResponse.UserCrewChallengeInfo(
                challenge.getId(),
                challenge.getCrewName(),
                challenge.getChallengeStatus(),
                challenge.getChallengePeriod().getDays(),
                crewMemberIds,
                crewCountDay,
                challenge.getStartDate()
        );
    }

}