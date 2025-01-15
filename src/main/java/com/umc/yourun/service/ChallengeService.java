package com.umc.yourun.service;

import com.umc.yourun.converter.ChallengeConverter;
import com.umc.yourun.domain.CrewChallenge;
import com.umc.yourun.dto.challenge.ChallengeRequest;
import com.umc.yourun.repository.CrewChallengeRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final CrewChallengeRepository crewChallengeRepository;

    @Transactional
    public Long createCrewChallenge(ChallengeRequest.CreateCrewChallengeReq request) {
        CrewChallenge crewChallenge = ChallengeConverter.toCrewChallenge(request);
        return crewChallengeRepository.save(crewChallenge).getId();
    }
}