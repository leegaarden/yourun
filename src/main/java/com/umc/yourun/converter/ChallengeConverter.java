package com.umc.yourun.converter;

import com.umc.yourun.domain.challenge.Challenge;
import com.umc.yourun.domain.enums.ChallengeKind;
import com.umc.yourun.dto.challenge.ChallengeRequest;
import com.umc.yourun.dto.challenge.ChallengeResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ChallengeConverter {

    // Request DTO -> Entity
    public static Challenge toChallenge(ChallengeRequest.CrewChallengeCreateReq request) {
        return Challenge.builder()
                .kind(ChallengeKind.CREW)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .crewName(request.getCrewName())
                .build();
    }

    public static Challenge toChallenge(ChallengeRequest.SoloChallengeCreateReq request) {
        return Challenge.builder()
                .kind(ChallengeKind.SOLO)
                .distance(request.getDistance())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
    }

    // Entity -> Response DTO
    public static ChallengeResponse.CrewChallengeResult toCrewChallengeResult(Challenge challenge) {
        return ChallengeResponse.CrewChallengeResult.builder()
                .id(challenge.getId())
                .startDate(challenge.getStartDate())
                .endDate(challenge.getEndDate())
                .crewName(challenge.getCrew().getName())
                .build();
    }

    public static ChallengeResponse.SoloChallengeResult toSoloChallengeResult(Challenge challenge) {
        return ChallengeResponse.SoloChallengeResult.builder()
                .id(challenge.getId())
                .distance(challenge.getDistance())
                .startDate(challenge.getStartDate())
                .endDate(challenge.getEndDate())
                .build();
    }
}
