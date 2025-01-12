package com.umc.yourun.converter;

import com.umc.yourun.domain.challenge.Challenge;
import com.umc.yourun.domain.enums.ChallengeKind;
import com.umc.yourun.dto.challenge.ChallengeRequest;
import com.umc.yourun.dto.challenge.ChallengeResponse;
import org.springframework.stereotype.Component;


@Component
public class ChallengeConverter {

    // Request DTO-> Entity
    public static Challenge toChallenge(ChallengeRequest.CrewChallengeCreateReq request) {
        return Challenge.builder()
                .kind(ChallengeKind.CREW)
                .startDate(request.startDate())
                .endDate(request.endDate())
                .crewName(request.crewName())
                .build();
    }

    public static Challenge toChallenge(ChallengeRequest.SoloChallengeCreateReq request) {
        return Challenge.builder()
                .kind(ChallengeKind.SOLO)
                .distance(request.distance())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();
    }

    // Entity -> Response DTO
    public static ChallengeResponse.CrewChallengeResult toCrewChallengeResult(Challenge challenge) {
        return new ChallengeResponse.CrewChallengeResult(
                challenge.getId(),
                challenge.getStartDate(),
                challenge.getEndDate(),
                challenge.getCrew().getName()
        );
    }

    public static ChallengeResponse.SoloChallengeResult toSoloChallengeResult(Challenge challenge) {
        return new ChallengeResponse.SoloChallengeResult(
                challenge.getId(),
                challenge.getDistance(),
                challenge.getStartDate(),
                challenge.getEndDate()
        );
    }
}
