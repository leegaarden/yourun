package com.umc.yourun.service;

import com.umc.yourun.converter.ChallengeConverter;
import com.umc.yourun.domain.challenge.Challenge;
import com.umc.yourun.dto.challenge.ChallengeRequest;
import com.umc.yourun.dto.challenge.ChallengeResponse;
import com.umc.yourun.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeRepository challengeRepository;

    public ChallengeResponse.CrewChallengeResult createCrewChallenge(ChallengeRequest.CrewChallengeCreateReq request) {
        Challenge challenge = ChallengeConverter.toChallenge(request);
        Challenge createdChallenge = challengeRepository.save(challenge);
        return ChallengeConverter.toCrewChallengeResult(createdChallenge);
    }

    public ChallengeResponse.SoloChallengeResult createSoloChallenge(ChallengeRequest.SoloChallengeCreateReq request) {
        Challenge challenge = ChallengeConverter.toChallenge(request);
        Challenge createdChallenge = challengeRepository.save(challenge);
        return ChallengeConverter.toSoloChallengeResult(createdChallenge);
    }
}