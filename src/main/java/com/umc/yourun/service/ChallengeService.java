package com.umc.yourun.service;

import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.GeneralException;
import com.umc.yourun.config.exception.custom.ChallengeException;
import com.umc.yourun.converter.ChallengeConverter;
import com.umc.yourun.domain.CrewChallenge;
import com.umc.yourun.domain.SoloChallenge;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.ChallengePeriod;
import com.umc.yourun.domain.enums.ChallengeStatus;
import com.umc.yourun.domain.mapping.UserSoloChallenge;
import com.umc.yourun.dto.challenge.ChallengeRequest;
import com.umc.yourun.dto.challenge.ChallengeResponse;
import com.umc.yourun.repository.CrewChallengeRepository;
import com.umc.yourun.repository.SoloChallengeRepository;
import com.umc.yourun.repository.UserRepository;
import com.umc.yourun.repository.UserSoloChallengeRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final CrewChallengeRepository crewChallengeRepository;
    private final SoloChallengeRepository soloChallengeRepository;
    private final UserSoloChallengeRepository userSoloChallengeRepository;
    private final UserRepository userRepository;

    // 크루 챌린지 생성
    @Transactional
    public Long createCrewChallenge(ChallengeRequest.CreateCrewChallengeReq request) {
        // 크루명 검사
        validateCrewName(request.crewName());

        // 날짜 검사 및 기간 반환
        ChallengePeriod period = validateDates(request.endDate());
        validateDates(request.endDate());
        CrewChallenge crewChallenge = ChallengeConverter.toCrewChallenge(request, period);
        return crewChallengeRepository.save(crewChallenge).getId();
    }

    // 솔로 챌린지 생성
    @Transactional
    public Long createSoloChallenge(ChallengeRequest.CreateSoloChallengeReq request, Long userId) {
        // 날짜 검사 및 기간 반환
        ChallengePeriod period = validateDates(request.endDate());

        // 솔로 챌린지 생성 및 저장
        SoloChallenge soloChallenge = ChallengeConverter.toSoloChallenge(request, period);
        SoloChallenge savedChallenge = soloChallengeRepository.save(soloChallenge);

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        // UserSoloChallenge 생성 및 저장
        UserSoloChallenge userSoloChallenge = UserSoloChallenge.builder()
                .user(user)
                .soloChallenge(savedChallenge)
                .build();
        userSoloChallengeRepository.save(userSoloChallenge);

        return savedChallenge.getId();
    }

    // PENDING 상태인 크루 챌린지 조회
    @Transactional(readOnly = true)
    public List<ChallengeResponse.CrewChallengeStatusRes> getPendingCrewChallenges() {
        List<CrewChallenge> pendingChallenges = crewChallengeRepository.findByChallengeStatus(ChallengeStatus.PENDING);
        return pendingChallenges.stream()
                .map(ChallengeConverter::toStatusCrewChallengeRes)
                .collect(Collectors.toList());
    }

    // IN_PROGRESS 상태인 크루 챌린지 조회
    @Transactional(readOnly = true)
    public List<ChallengeResponse.CrewChallengeStatusRes> getInProgressCrewChallenges() {
        List<CrewChallenge> inProgressChallenges = crewChallengeRepository.findByChallengeStatus(ChallengeStatus.IN_PROGRESS);
        return inProgressChallenges.stream()
                .map(ChallengeConverter::toStatusCrewChallengeRes)
                .collect(Collectors.toList());
    }

    // PENDING 상태인 솔로 챌린지 조회
    @Transactional(readOnly = true)
    public List<ChallengeResponse.SoloChallengeStatusRes> getPendingSoloChallenges() {
        List<SoloChallenge> pendingChallenges = soloChallengeRepository.findByChallengeStatus(ChallengeStatus.PENDING);
        return pendingChallenges.stream()
                .map(ChallengeConverter::toStatusSoloChallengeRes)
                .collect(Collectors.toList());
    }

    // IN_PROGRESS 상태인 솔로 챌린지 조회
    @Transactional(readOnly = true)
    public List<ChallengeResponse.SoloChallengeStatusRes> getInProgressSoloChallenges() {
        List<SoloChallenge> inProgressChallenges = soloChallengeRepository.findByChallengeStatus(ChallengeStatus.IN_PROGRESS);
        return inProgressChallenges.stream()
                .map(ChallengeConverter::toStatusSoloChallengeRes)
                .collect(Collectors.toList());
    }

    // 솔로 챌린지에 참여하기
    @Transactional
    public ChallengeResponse.ChallengeMateRes joinSoloChallenge(Long challengeId, Long userId) {
        // 1. 챌린지 조회
        SoloChallenge soloChallenge = soloChallengeRepository.findById(challengeId)
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

        // 2. 챌린지 상태 확인
        if (soloChallenge.getChallengeStatus() != ChallengeStatus.PENDING) {
            throw new ChallengeException(ErrorCode.INVALID_CHALLENGE_STATUS);
        }

        // 3. 24시간 이내 챌린지인지 확인
        if (!soloChallenge.isMatchable()) {
            throw new ChallengeException(ErrorCode.CHALLENGE_EXPIRED);
        }

        // 4. 이미 진행 중인 챌린지가 있는지 확인
        if (userSoloChallengeRepository.existsByUserIdAndSoloChallenge_ChallengeStatus(
                userId, ChallengeStatus.IN_PROGRESS)) {
            throw new ChallengeException(ErrorCode.ALREADY_IN_CHALLENGE);
        }

        // 5. 챌린지 생성자 확인
        UserSoloChallenge creatorChallenge = userSoloChallengeRepository.findBySoloChallengeId(challengeId)
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

        // 6. 본인 챌린지 참여 방지
        if (creatorChallenge.getUser().getId().equals(userId)) {
            throw new ChallengeException(ErrorCode.CANNOT_JOIN_OWN_CHALLENGE);
        }

        // 7. UserSoloChallenge 생성 및 저장

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        UserSoloChallenge userSoloChallenge = UserSoloChallenge.builder()
                .user(User.builder().id(userId).build())
                .soloChallenge(soloChallenge)
                .build();
        userSoloChallengeRepository.save(userSoloChallenge);

        // 8. 챌린지 상태 업데이트
        soloChallenge.updateStatus(ChallengeStatus.IN_PROGRESS);

        return new ChallengeResponse.ChallengeMateRes(challengeId, creatorChallenge.getUser().getId());
    }


    // 크루 이름 검사
    // TODO: 한번에 검사하는 로직으로
    private void validateCrewName(String crewName) {
        if (crewName == null || crewName.trim().isEmpty()) {
            throw new ChallengeException(ErrorCode.INVALID_CREW_NAME_NULL);
        }
        if (crewName.matches(".*[!@#$%^&*(),.?\":{}|<>].*") || crewName.matches(".*[a-zA-Z].*")) {
            throw new ChallengeException(ErrorCode.INVALID_CREW_NAME_FORMAT1);
        }
        if (crewName.length() < 3 || crewName.length() > 5) {
            throw new ChallengeException(ErrorCode.INVALID_CREW_NAME_FORMAT2);
        }
    }

    // 기간 검사
    private ChallengePeriod validateDates(LocalDate endDate) {
        LocalDate startDate = LocalDate.now().plusDays(1);

        if (!startDate.equals(LocalDate.now().plusDays(1))) {
            throw new ChallengeException(ErrorCode.INVALID_START_DATE);
        }
        if (endDate.isBefore(startDate) || endDate.equals(startDate)) {
            throw new ChallengeException(ErrorCode.INVALID_END_DATE);
        }

        long period = ChronoUnit.DAYS.between(startDate, endDate);
        if (period < 3 || period > 5) {
            throw new ChallengeException(ErrorCode.INVALID_CHALLENGE_PERIOD);
        }

        return ChallengePeriod.from(period);  // 기간 반환
    }

}