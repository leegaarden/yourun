package com.umc.yourun.converter;

import com.umc.yourun.domain.CrewChallenge;
import com.umc.yourun.domain.SoloChallenge;
import com.umc.yourun.domain.enums.ChallengePeriod;
import com.umc.yourun.dto.challenge.ChallengeRequest;
import com.umc.yourun.dto.challenge.ChallengeResponse;
import org.springframework.stereotype.Component;

@Component
public class ChallengeConverter {


    // request DTO -> Entity
    public static CrewChallenge toCrewChallenge(ChallengeRequest.CreateCrewChallengeReq request, ChallengePeriod challengePeriod) {
        return CrewChallenge.builder()
                .crewName(request.crewName())
                .endDate(request.endDate())
                .challengePeriod(challengePeriod)
                .build();
    }

    public static SoloChallenge toSoloChallenge(ChallengeRequest.CreateSoloChallengeReq request, ChallengePeriod challengePeriod) {
        return SoloChallenge.builder()
                .endDate(request.endDate())
                .challengeDistance(request.challengeDistance())
                .challengePeriod(challengePeriod)
                .build();
    }

    public static ChallengeResponse.CrewChallengeStatusRes toStatusResponse(CrewChallenge challenge) {
        return new ChallengeResponse.CrewChallengeStatusRes(
                challenge.getId(),
                challenge.getCrewName(),
                challenge.getStartDate(),
                challenge.getEndDate(),
                challenge.getChallengePeriod()
        );
    }

}