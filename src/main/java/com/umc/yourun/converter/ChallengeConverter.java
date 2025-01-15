package com.umc.yourun.converter;

import com.umc.yourun.domain.CrewChallenge;
import com.umc.yourun.domain.SoloChallenge;
import com.umc.yourun.dto.challenge.ChallengeRequest;
import org.springframework.stereotype.Component;

@Component
public class ChallengeConverter {


    // request DTO -> Entity
    public static CrewChallenge toCrewChallenge(ChallengeRequest.CreateCrewChallengeReq request) {
        return CrewChallenge.builder()
                .crewName(request.crewName())
                .endDate(request.endDate())
                .build();
    }

    public static SoloChallenge toSoloChallenge(ChallengeRequest.CreateSoloChallengeReq request) {
        return SoloChallenge.builder()
                .endDate(request.endDate())
                .challengeDistance(request.challengeDistance())
                .build();
    }
}