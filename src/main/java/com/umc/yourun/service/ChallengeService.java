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
import com.umc.yourun.domain.mapping.UserCrewChallenge;
import com.umc.yourun.domain.mapping.UserSoloChallenge;
import com.umc.yourun.dto.challenge.ChallengeRequest;
import com.umc.yourun.dto.challenge.ChallengeResponse;
import com.umc.yourun.repository.*;
import com.umc.yourun.service.ChallengeMatchService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final CrewChallengeRepository crewChallengeRepository;
    private final SoloChallengeRepository soloChallengeRepository;
    private final UserSoloChallengeRepository userSoloChallengeRepository;
    private final UserCrewChallengeRepository userCrewChallengeRepository;
    private final UserRepository userRepository;
    private final ChallengeMatchService challengeMatchService;

    // 크루 챌린지 생성
    @Transactional
    public Long createCrewChallenge(ChallengeRequest.CreateCrewChallengeReq request, Long userId) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        // 이미 진행 중 (혹은 대기) 인 크루 챌린지가 있는지 검사
        if (userCrewChallengeRepository.existsByUserIdAndCrewChallenge_ChallengeStatusIn(
                userId,
                Arrays.asList(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS))) {

            UserCrewChallenge userCrewChallenge = userCrewChallengeRepository.findByUserId(userId);

            // 사용자가 생성자였던 경우
            if (userCrewChallenge.isCreator()) {
                throw new ChallengeException(ErrorCode.INVALID_CHALLENGE_CREATE);
            } else {
                throw new ChallengeException(ErrorCode.INVALID_CHALLENGE_JOIN);
            }

        }

        // 크루명 검사
        validateCrewName(request.crewName());

        // 날짜 검사 및 기간 반환
        ChallengePeriod period = validateDates(request.endDate());

        CrewChallenge crewChallenge = ChallengeConverter.toCrewChallenge(request, period);
        CrewChallenge savedCrewChallenge = crewChallengeRepository.save(crewChallenge);

        UserCrewChallenge userCrewChallenge = ChallengeConverter.toUserCrewChallenge(user, savedCrewChallenge, true);
        userCrewChallengeRepository.save(userCrewChallenge);

        return savedCrewChallenge.getId();
    }

    // 솔로 챌린지 생성
    @Transactional
    public Long createSoloChallenge(ChallengeRequest.CreateSoloChallengeReq request, Long userId) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        // 이미 진행 중(혹은 대기) 인 솔로 챌린지가 있는지 검사
        if (userSoloChallengeRepository.existsByUserIdAndSoloChallenge_ChallengeStatusIn(
                userId,
                Arrays.asList(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS))) {

            UserSoloChallenge userSoloChallenge = userSoloChallengeRepository.findByUserId(userId);

            // 사용자가 생성자였던 경우
            if (userSoloChallenge.isCreator()) {
                throw new ChallengeException(ErrorCode.INVALID_CHALLENGE_CREATE);
            } else {
                throw new ChallengeException(ErrorCode.INVALID_CHALLENGE_JOIN);
            }

        }

        // 날짜 검사 및 기간 반환
        ChallengePeriod period = validateDates(request.endDate());

        // 솔로 챌린지 생성 및 저장
        SoloChallenge soloChallenge = ChallengeConverter.toSoloChallenge(request, period);
        SoloChallenge savedChallenge = soloChallengeRepository.save(soloChallenge);

        // UserSoloChallenge 생성 및 저장
        UserSoloChallenge userSoloChallenge = ChallengeConverter.toUserSoloChallenge(user, savedChallenge, true);
        userSoloChallengeRepository.save(userSoloChallenge);

        return savedChallenge.getId();
    }

    // PENDING 상태인 크루 챌린지 조회
    @Transactional(readOnly = true)
    public List<ChallengeResponse.CrewChallengeStatusRes> getPendingCrewChallenges(Long userId) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        List<CrewChallenge> pendingChallenges = crewChallengeRepository.findByChallengeStatus(ChallengeStatus.PENDING);
        return pendingChallenges.stream()
                .map(ChallengeConverter::toStatusCrewChallengeRes)
                .collect(Collectors.toList());
    }

    // IN_PROGRESS 상태인 크루 챌린지 조회
    @Transactional(readOnly = true)
    public List<ChallengeResponse.CrewChallengeStatusRes> getInProgressCrewChallenges(Long userId) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        List<CrewChallenge> inProgressChallenges = crewChallengeRepository.findByChallengeStatus(ChallengeStatus.IN_PROGRESS);
        return inProgressChallenges.stream()
                .map(ChallengeConverter::toStatusCrewChallengeRes)
                .collect(Collectors.toList());
    }

    // PENDING 상태인 솔로 챌린지 조회
    @Transactional(readOnly = true)
    public List<ChallengeResponse.SoloChallengeStatusRes> getPendingSoloChallenges(Long userId) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        List<SoloChallenge> pendingChallenges = soloChallengeRepository.findByChallengeStatus(ChallengeStatus.PENDING);
        return pendingChallenges.stream()
                .map(ChallengeConverter::toStatusSoloChallengeRes)
                .collect(Collectors.toList());
    }

    // IN_PROGRESS 상태인 솔로 챌린지 조회
    @Transactional(readOnly = true)
    public List<ChallengeResponse.SoloChallengeStatusRes> getInProgressSoloChallenges(Long userId) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        List<SoloChallenge> inProgressChallenges = soloChallengeRepository.findByChallengeStatus(ChallengeStatus.IN_PROGRESS);
        return inProgressChallenges.stream()
                .map(ChallengeConverter::toStatusSoloChallengeRes)
                .collect(Collectors.toList());
    }

    // 솔로 챌린지에 참여하기
    @Transactional
    public ChallengeResponse.ChallengeMateRes joinSoloChallenge(Long challengeId, Long userId) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        // 1. 챌린지 조회
        SoloChallenge soloChallenge = soloChallengeRepository.findById(challengeId)
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

        // 2. 챌린지 상태 확인 (해당 챌린지가 이미 진행 중인 챌린지 인지 검사)
        if (soloChallenge.getChallengeStatus() != ChallengeStatus.PENDING) {
            throw new ChallengeException(ErrorCode.INVALID_CHALLENGE_STATUS);
        }

        // 3. 24시간 이내 챌린지인지 확인
        if (!soloChallenge.isMatchable()) {
            throw new ChallengeException(ErrorCode.CHALLENGE_EXPIRED);
        }

        // 챌린지 생성자
        UserSoloChallenge creatorChallenge = userSoloChallengeRepository.findBySoloChallengeId(challengeId)
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

        // 4. 사용자가 이미 진행 중(혹은 대기 중) 인 솔로 챌린지가 있는지 확인
        if (userSoloChallengeRepository.existsByUserIdAndSoloChallenge_ChallengeStatusIn(
                userId, Arrays.asList(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS))) {

            // 5. 본인 챌린지 참여 방지
            if (creatorChallenge.getUser().getId().equals(userId)) {
                throw new ChallengeException(ErrorCode.CANNOT_JOIN_OWN_CHALLENGE);
            } else {
                throw new ChallengeException(ErrorCode.INVALID_CHALLENGE_JOIN);
            }

        }

        // 6. UserSoloChallenge 생성 및 저장
        UserSoloChallenge userSoloChallenge = ChallengeConverter.toUserSoloChallenge(
                User.builder().id(userId).build(),
                soloChallenge,
                false);
        userSoloChallengeRepository.save(userSoloChallenge);

        // 8. 챌린지 상태 업데이트
        soloChallenge.updateStatus(ChallengeStatus.IN_PROGRESS);

        return new ChallengeResponse.ChallengeMateRes(challengeId, creatorChallenge.getUser().getId());
    }

    // 크루 챌린지에 참여하기
    @Transactional
    public ChallengeResponse.CrewChallengeMateRes joinCrewChallenge(Long challengeId, Long userId) {
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        // 1. 챌린지 조회
        CrewChallenge crewChallenge = crewChallengeRepository.findById(challengeId)
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

        // 2. 챌린지 상태 확인
        if (crewChallenge.getChallengeStatus() != ChallengeStatus.PENDING) {
            throw new ChallengeException(ErrorCode.INVALID_CHALLENGE_STATUS);
        }

        // 3. 24시간 이내 챌린지인지 확인
        if (!crewChallenge.isMatchable()) {
            throw new ChallengeException(ErrorCode.CHALLENGE_EXPIRED);
        }

        // 4. 현재 참여 인원 확인 (4명 인원 제한)
        long countCurrentParticipants = userCrewChallengeRepository.countByCrewChallengeId(challengeId);
        if (countCurrentParticipants >= 4) {
            throw new ChallengeException(ErrorCode.CREW_CHALLENGE_FULL);
        }

        // 5. 이미 진행 중인 크루 챌린지가 있는지 확인 및 셀프 참여 방지
        UserCrewChallenge existUserCrewChallenge = userCrewChallengeRepository
                .findByUserIdAndCrewChallenge_ChallengeStatusIn(
                        userId,
                        Arrays.asList(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS));

        UserCrewChallenge creator = userCrewChallengeRepository
                .findByCrewChallengeIdAndIsCreator(challengeId, true)
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

        if (existUserCrewChallenge != null) {

            if (creator.getUser().getId().equals(userId)) {
                throw new ChallengeException(ErrorCode.CANNOT_JOIN_OWN_CHALLENGE);
            } else {
                throw new ChallengeException(ErrorCode.INVALID_CHALLENGE_JOIN);
            }
        }

        // 6. UserCrewChallenge 생성 및 저장
        UserCrewChallenge userCrewChallenge = ChallengeConverter.toUserCrewChallenge(
                user,
                crewChallenge,
                false);
        userCrewChallengeRepository.save(userCrewChallenge);

        // 7. 챌린지 참여자 조회
        List<Long> participants = userCrewChallengeRepository
                .findByCrewChallengeIdOrderByCreatedAt(challengeId)
                .stream()
                .map(challenge -> challenge.getUser().getId())
                .collect(Collectors.toList());

        return new ChallengeResponse.CrewChallengeMateRes(challengeId, participants);
    }

    // 크루 챌린지 매칭 화면
    @Transactional(readOnly = true)
    public ChallengeResponse.CrewMatchingRes getCrewMatch(Long userId) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        // 1. 사용자의 현재 크루 챌린지 참여 정보 조회
        UserCrewChallenge userCrewChallenge = userCrewChallengeRepository
                .findByUserIdAndCrewChallenge_ChallengeStatusIn(userId,
                        List.of(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS));
                // .orElseThrow(() -> new ChallengeException(ErrorCode.NO_CREW_CHALLENGE_FOUND));

        if (userCrewChallenge == null) {
            throw new GeneralException(ErrorCode.NO_CREW_CHALLENGE_FOUND);
        }

        // 2. 내 크루원 ID 목록 조회 (참여 순서대로)

        CrewChallenge myCrew = userCrewChallenge.getCrewChallenge();

        List<Long> crewMemberIds = userCrewChallengeRepository
                .findByCrewChallengeIdOrderByCreatedAt(myCrew.getId())
                .stream()
                .map(uc -> uc.getUser().getId())
                .collect(Collectors.toList());

        // 3. 매칭된 크루 정보 조회
        String matchedCrewName = null;
        List<Long> matchedCrewMemberIds = new ArrayList<>();

        if (myCrew.getMatchedCrewChallengeId() != null) {
            CrewChallenge matchedCrew = crewChallengeRepository
                    .findById(myCrew.getMatchedCrewChallengeId())
                    .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

            matchedCrewName = matchedCrew.getCrewName();
            matchedCrewMemberIds = userCrewChallengeRepository
                    .findByCrewChallengeIdOrderByCreatedAt(matchedCrew.getId())
                    .stream()
                    .map(uc -> uc.getUser().getId())
                    .collect(Collectors.toList());
        }

        return ChallengeConverter.toCrewMatchingRes(
                myCrew,
                crewMemberIds,
                matchedCrewName,
                matchedCrewMemberIds
        );
    }

    // 홈 화면에서 유저의 챌린지 관련 화면 조회
    @Transactional(readOnly = true)
    public ChallengeResponse.HomeChallengeRes getUserChallenges(Long userId) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        // 솔로 챌린지 조회
        UserSoloChallenge userSoloChallenge = userSoloChallengeRepository
                .findByUserIdAndSoloChallenge_ChallengeStatusIn(
                        userId,
                        List.of(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS))
                .orElse(null);

        // 크루 챌린지 조회 (4명이 결성된 크루일 경우에만)
        UserCrewChallenge userCrewChallenge = userCrewChallengeRepository
                .findByUserIdAndCrewChallenge_ChallengeStatusIn(
                        userId,
                        List.of(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS));

        // 응답 DTO 생성
        ChallengeResponse.UserSoloChallengeInfo soloInfo = null;
        if (userSoloChallenge != null) {
            SoloChallenge challenge = userSoloChallenge.getSoloChallenge();
            int soloCountDay = calculateCountDay(challenge.getStartDate());

            // 챌린지 메이트 조회
            Long mateId = null;
            if (challenge.getChallengeStatus() == ChallengeStatus.IN_PROGRESS) {
                mateId = userSoloChallengeRepository
                        .findBySoloChallengeIdAndUserIdNot(challenge.getId(), userId)
                        .map(mate -> mate.getUser().getId())
                        .orElse(null);
            }

            soloInfo = ChallengeConverter.toUserSoloChallengeInfo(challenge, userId, mateId, soloCountDay);
        }

        ChallengeResponse.UserCrewChallengeInfo crewInfo = null;
        if (userCrewChallenge != null) {
            CrewChallenge challenge = userCrewChallenge.getCrewChallenge();
            int crewCountDay = calculateCountDay(challenge.getStartDate());

            // 크루원 수 확인
            long memberCount = userCrewChallengeRepository.countByCrewChallengeId(challenge.getId());
            if (memberCount == 4) {  // 4명이 모인 크루만 응답
                List<Long> crewMemberIds = userCrewChallengeRepository
                        .findByCrewChallengeIdOrderByCreatedAt(challenge.getId())
                        .stream()
                        .map(member -> member.getUser().getId())
                        .collect(Collectors.toList());

                crewInfo = ChallengeConverter.toUserCrewChallengeInfo(challenge, crewMemberIds, crewCountDay);
            }
        }

        return new ChallengeResponse.HomeChallengeRes(soloInfo, crewInfo);
    }

    // 크루 이름 검사
    // TODO: 한번에 검사하는 로직으로
    private void validateCrewName(String crewName) {
        // null 체크
        if (crewName == null || crewName.trim().isEmpty()) {
            throw new ChallengeException(ErrorCode.INVALID_CREW_NAME_NULL);
        }

        // 길이 검사 (2-5자)
        if (crewName.length() < 2 || crewName.length() > 5) {
            throw new ChallengeException(ErrorCode.INVALID_CREW_NAME_FORMAT2);
        }

        // 한글만 허용하고 띄어쓰기 없는지 검사
        if (!crewName.matches("^[가-힣]{2,5}$")) {
            throw new ChallengeException(ErrorCode.INVALID_CREW_NAME_FORMAT1);
        }

        // 크루명 중복 검사
        if (crewChallengeRepository.existsByCrewNameIgnoreCase(crewName)) {
            throw new ChallengeException(ErrorCode.DUPLICATE_CREW_NAME);
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

    // 챌린지 며칠째 진행 중인지
    private int calculateCountDay(LocalDate startDate) {
        return (int) ChronoUnit.DAYS.between(startDate, LocalDate.now()) + 1;
    }


}