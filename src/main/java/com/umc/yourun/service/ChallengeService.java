package com.umc.yourun.service;

import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.custom.ChallengeException;
import com.umc.yourun.converter.ChallengeConverter;
import com.umc.yourun.domain.challenge.Challenge;
import com.umc.yourun.dto.challenge.ChallengeRequest;
import com.umc.yourun.dto.challenge.ChallengeResponse;
import com.umc.yourun.repository.ChallengeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeRepository challengeRepository;

    public ChallengeResponse.CrewChallengeResult createCrewChallenge(ChallengeRequest.CrewChallengeCreateReq request) {

        validateCrewName(request.crewName());
        validateDateRange(request.startDate(), request.endDate());
        
        Challenge challenge = ChallengeConverter.toChallenge(request);
        Challenge createdChallenge = challengeRepository.save(challenge);
        return ChallengeConverter.toCrewChallengeResult(createdChallenge);
    }

    public ChallengeResponse.SoloChallengeResult createSoloChallenge(ChallengeRequest.SoloChallengeCreateReq request) {

        validateDateRange(request.startDate(), request.endDate());

        Challenge challenge = ChallengeConverter.toChallenge(request);
        Challenge createdChallenge = challengeRepository.save(challenge);
        return ChallengeConverter.toSoloChallengeResult(createdChallenge);
    }

    private void validateCrewName(String crewName) {
        // null 또는 빈 문자열 체크
        if (crewName == null || crewName.trim().isEmpty()) {
            throw new ChallengeException(ErrorCode.INVALID_CREW_NAME_NULL);
        }

        // 특수문자 체크 (영문, 한글, 숫자만 허용)
        if (!crewName.matches("^[가-힣a-zA-Z0-9\\s]+$")) {
            throw new ChallengeException(ErrorCode.INVALID_CREW_NAME_FORMAT);
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();

        if (startDate.isBefore(today)) {
            throw new ChallengeException(ErrorCode.INVALID_START_DATE);
        }

        if (endDate.isBefore(startDate)) {
            throw new ChallengeException(ErrorCode.INVALID_END_DATE);
        }

        long challengeDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (challengeDays < 3 || challengeDays > 5) {
            throw new ChallengeException(ErrorCode.INVALID_CHALLENGE_PERIOD);
        }
    }
}