package com.umc.yourun.service.challenge;

import com.umc.yourun.config.JwtTokenProvider;
import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.GeneralException;
import com.umc.yourun.config.exception.custom.ChallengeException;
import com.umc.yourun.converter.ChallengeConverter;
import com.umc.yourun.domain.CrewChallenge;
import com.umc.yourun.domain.RunningData;
import com.umc.yourun.domain.SoloChallenge;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.*;
import com.umc.yourun.domain.mapping.UserCrewChallenge;
import com.umc.yourun.domain.mapping.UserSoloChallenge;
import com.umc.yourun.dto.challenge.ChallengeRequest;
import com.umc.yourun.dto.challenge.CrewChallengeResponse;
import com.umc.yourun.dto.challenge.SoloChallengeResponse;
import com.umc.yourun.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoloChallengeService {

    private final SoloChallengeRepository soloChallengeRepository;
    private final UserSoloChallengeRepository userSoloChallengeRepository;
    private final UserCrewChallengeRepository userCrewChallengeRepository;
    private final UserRepository userRepository;
    private final CrewChallengeService crewChallengeService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RunningDataRepository runningDataRepository;

    // 1. 솔로 챌린지 생성
    @Transactional
    public SoloChallengeResponse.SoloChallengeCreateRes createSoloChallenge(ChallengeRequest.CreateSoloChallengeReq request, String accessToken) {

        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 현재 시간 기준으로 시작 시간 설정 (다음날 같은 시간) 및 종료 시간
        LocalDateTime startDateTime = parseDateTime(formatDateTime(LocalDateTime.now().plusDays(1)));
        // 종료일에 현재 시간 결합
        LocalDateTime endDateTime = parseDateTime(formatDateTime(LocalDateTime.of(
                request.endDate(),
                startDateTime.toLocalTime()
        )));

        // 이미 진행 중(혹은 대기) 인 솔로 챌린지가 있는지 검사
        if (userSoloChallengeRepository.existsByUserIdAndSoloChallenge_ChallengeStatusIn(
                user.getId(),
                Arrays.asList(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS))) {

            UserSoloChallenge userSoloChallenge = userSoloChallengeRepository
                    .findFirstByUserIdOrderByCreatedAtDesc(user.getId())
                    .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

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
        SoloChallenge soloChallenge = ChallengeConverter.toSoloChallenge(request, startDateTime, endDateTime, period);
        SoloChallenge savedSoloChallenge = soloChallengeRepository.save(soloChallenge);

        // UserSoloChallenge 생성 및 저장
        UserSoloChallenge userSoloChallenge = ChallengeConverter.toUserSoloChallenge(user, savedSoloChallenge, true);
        userSoloChallengeRepository.save(userSoloChallenge);

        return new SoloChallengeResponse.SoloChallengeCreateRes(savedSoloChallenge.getId(),
                formatDateTime(startDateTime), formatDateTime(endDateTime),
                savedSoloChallenge.getChallengePeriod().getDays(), user.getTendency());
    }

    // 2. PENDING 상태인 솔로 챌린지 조회 (화면)
    @Transactional(readOnly = true)
    public SoloChallengeResponse.SoloChallengeRes getPendingSoloChallenges(String accessToken) {
        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);
        List<SoloChallengeResponse.SoloChallenge> pendingSoloChallenges = getPendingSoloChallenge();

        return new SoloChallengeResponse.SoloChallengeRes(user.getId(), user.getTendency(),
                user.getCrewReward(), user.getPersonalReward(), pendingSoloChallenges);
    }

    // 2-1. PENDING 상태인 솔로 챌린지
    @Transactional(readOnly = true)
    public List<SoloChallengeResponse.SoloChallenge> getPendingSoloChallenge() {


        List<SoloChallenge> pendingChallenges = soloChallengeRepository.findRandomPendingChallenges(5);

        return pendingChallenges.stream()
                .map(challenge -> {
                    // 챌린지 생성자 조회
                    UserSoloChallenge creator = userSoloChallengeRepository
                            .findBySoloChallengeIdAndIsCreator(challenge.getId(), true)
                            .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

                    // 생성자의 닉네임과 해시태그 조회
                    User creatorUser = creator.getUser();
                    String nickname = userRepository.findNicknameById(creatorUser.getId())
                            .orElse("Unknown"); // 이럴 일은 없지만 에러 때문에

                    // 해시태그 값 조회 (최대 2개)
                    List<String> hashtags = creatorUser.getUserTags().stream()
                            .map(userTag -> userTag.getTag().name())  // UserTag 엔티티에서 Tag enum의 이름을 가져옴
                            .limit(2)  // 최대 2개로 제한
                            .collect(Collectors.toList());

                    // 기간에 따른 보상 계산
                    int reward = switch (challenge.getChallengePeriod().getDays()) {
                        case 3 -> 1;
                        case 4 -> 2;
                        case 5 -> 3;
                        default -> 0;
                    };

                    return new SoloChallengeResponse.SoloChallenge(
                            challenge.getId(),
                            challenge.getChallengeDistance().getDistance(),
                            challenge.getChallengePeriod().getDays(),
                            nickname,
                            hashtags,
                            reward,
                            creatorUser.getTendency()
                    );
                })
                .collect(Collectors.toList());
    }

    // 3. 솔로 챌린지 상세 조회
    @Transactional(readOnly = true)
    public SoloChallengeResponse.SoloChallengeDetailRes getSoloChallengeDetail(Long challengeId, String accessToken) {

        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 솔로 챌린지 조회
        Optional<SoloChallenge> soloChallenge = soloChallengeRepository.findById(challengeId);

        // 챌린지 생성자 조회
        UserSoloChallenge creator = userSoloChallengeRepository
                .findBySoloChallengeIdAndIsCreator(challengeId, true)
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

        // 생성자의 닉네임과 해시태그 조회
        User creatorUser = creator.getUser();
        String nickname = userRepository.findNicknameById(creatorUser.getId())
                .orElse("Unknown"); // 이럴 일은 없지만 에러 때문에

        // 해시태그 값 조회 (최대 2개)
        List<String> hashtags = creatorUser.getUserTags().stream()
                .map(userTag -> userTag.getTag().name())  // UserTag 엔티티에서 Tag enum의 이름을 가져옴
                .limit(2)  // 최대 2개로 제한
                .collect(Collectors.toList());

        // 기간에 따른 보상 계산
        int reward = switch (soloChallenge.get().getChallengePeriod().getDays()) {
            case 3 -> 1;
            case 4 -> 2;
            case 5 -> 3;
            default -> 0;
        };

        int countDay = calculateCountDay(user.getCreatedAt().toLocalDate());

        return new SoloChallengeResponse.SoloChallengeDetailRes(formatDateTime(soloChallenge.get().getStartDate()),
                formatDateTime(soloChallenge.get().getEndDate()), soloChallenge.get().getChallengeDistance().getDistance(),
                soloChallenge.get().getChallengePeriod().getDays(), nickname, hashtags, creatorUser.getTendency(),
                reward, countDay);
    }

    // 4. 솔로 챌린지 매칭 화면
    @Transactional(readOnly = true)
    public SoloChallengeResponse.SoloChallengeMatchingRes getSoloChallengeMatching(String accessToken) {

        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 유저가 참여 중인 챌린지 조회
        UserSoloChallenge userSoloChallenge = userSoloChallengeRepository
                .findFirstByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));
        SoloChallenge soloChallenge = userSoloChallenge.getSoloChallenge();

        // 챌린지 메이트 조회
        Long mateId = userSoloChallengeRepository
                .findBySoloChallengeIdAndUserIdNot(soloChallenge.getId(), user.getId())
                .map(mate -> mate.getUser().getId())
                .orElse(null);

        Optional<User> challengeMate = userRepository.findById(mateId);

        // 해시 태그
        List<String> userHashtags = user.getUserTags().stream()
                .map(userTag -> userTag.getTag().name())  // UserTag 엔티티에서 Tag enum의 이름을 가져옴
                .limit(2)  // 최대 2개로 제한
                .toList();

        List<String> challengeHashtags = challengeMate.get().getUserTags().stream()
                .map(userTag -> userTag.getTag().name())  // UserTag 엔티티에서 Tag enum의 이름을 가져옴
                .limit(2)  // 최대 2개로 제한
                .toList();

        // 앱 사용 기간
        int userCountDay = calculateCountDay(user.getCreatedAt().toLocalDate());
        int challengeMateCountDay = calculateCountDay(challengeMate.get().getCreatedAt().toLocalDate());

        return new SoloChallengeResponse.SoloChallengeMatchingRes(soloChallenge.getChallengePeriod().getDays(),
                soloChallenge.getChallengeDistance().getDistance(), user.getTendency(),
                user.getNickname(), userCountDay, userHashtags, challengeMate.get().getTendency(),
                challengeMate.get().getNickname(), challengeMateCountDay, challengeHashtags);

    }

    // 5. 솔로 챌린지 일자별 진행도
    public SoloChallengeResponse.SoloChallengeProgressRes getSoloChallengeProgress(String accessToken) {

        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 유저가 현재 참여 중인 챌린지 조회
        UserSoloChallenge userSoloChallenge = userSoloChallengeRepository
                .findFirstByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));
        SoloChallenge soloChallenge = userSoloChallenge.getSoloChallenge();

        // 챌린지 메이트 조회
        Long mateId = userSoloChallengeRepository
                .findBySoloChallengeIdAndUserIdNot(soloChallenge.getId(), user.getId())
                .map(mate -> mate.getUser().getId())
                .orElse(null);

        Optional<User> challengeMate = userRepository.findById(mateId);

        // 일자별 러닝 데이터 조회
        List<SoloChallengeResponse.DayResult> dayResults = getDayResults(user.getId(), soloChallenge);

        // 챌린지 메이트의 거리 및 정보 조회
        SoloChallengeResponse.ChallengeMateInfo mateInfo = getChallengeMateInfo(challengeMate.get(), soloChallenge);

        // 유저의 금일 성공 여부
        boolean userIsSuccess = checkTodaySuccess(user.getId(), soloChallenge);

        return new SoloChallengeResponse.SoloChallengeProgressRes(soloChallenge.getChallengePeriod().getDays(),
                soloChallenge.getChallengeDistance().getDistance(),
                calculateDayCount(soloChallenge.getStartDate()),
                formatDateTime(LocalDateTime.now()),
                dayResults,
                mateInfo,
                userIsSuccess,
                user.getTendency()
        );

    }

    // 5-1. 일자별 달린 거리
    private List<SoloChallengeResponse.DayResult> getDayResults(Long userId, SoloChallenge challenge) {
        LocalDateTime startDate = challenge.getStartDate();
        LocalDateTime now = parseDateTime(formatDateTime(LocalDateTime.now()));

        int currentDay = calculateDayFromStart(startDate, now);
        List<SoloChallengeResponse.DayResult> results = new ArrayList<>();

        for (int day = 1; day <= currentDay; day++) {
            LocalDateTime dayStart = challenge.getStartDate().plusDays(day - 1);
            LocalDateTime dayEnd = day == currentDay
                    ? now
                    : challenge.getStartDate().plusDays(day);

            List<RunningData> dayRunningData = runningDataRepository
                    .findAllByUserIdAndStartTimeBetweenAndStatus(
                            userId,
                            dayStart,
                            dayEnd,
                            RunningDataStatus.ACTIVE.name()
                    );

            double totalDistance = dayRunningData.stream()
                    .mapToDouble(data -> data.getTotalDistance() / 1000.0)
                    .sum();

            boolean isSuccess = totalDistance >= challenge.getChallengeDistance().getDistance();

            results.add(new SoloChallengeResponse.DayResult(day, totalDistance, isSuccess));
        }

        return results.stream()
                .sorted(Comparator.comparing(SoloChallengeResponse.DayResult::day))
                .collect(Collectors.toList());
    }

    // 5-2. 챌린지 메이트 달린 거리 및 정보
    private SoloChallengeResponse.ChallengeMateInfo getChallengeMateInfo(User mate, SoloChallenge challenge) {
        LocalDateTime now = parseDateTime(formatDateTime(LocalDateTime.now())); // 시간과 분만 비교할 수 있도록 포멧팅
        int currentDay = calculateDayFromStart(challenge.getStartDate(), now);

        // 메이트의 챌린지 상태 조회
        UserSoloChallenge challengeMate = userSoloChallengeRepository
                .findBySoloChallengeIdAndUserIdNot(challenge.getId(), mate.getId())
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

        // 실패 상태인 경우, 실패한 날짜 이전까지만 계산
        if (challengeMate.getChallengeResult() == ChallengeResult.FAILURE) {
            // 실패한 날짜 조회 (실패한 날의 시작시간 ~ 종료시간 사이의 데이터로 판단)
            for (int day = 1; day <= currentDay; day++) {
                // 각 일차의 시작시간과 종료시간 계산
                LocalDateTime dayStart = challenge.getStartDate().plusDays(day - 1);
                LocalDateTime dayEnd = challenge.getStartDate().plusDays(day);

                double dayDistance = runningDataRepository
                        .findAllByUserIdAndStartTimeBetweenAndStatus(
                                mate.getId(),
                                dayStart,
                                dayEnd,
                                RunningDataStatus.ACTIVE.name()
                        )
                        .stream()
                        .mapToDouble(data -> data.getTotalDistance() / 1000.0)
                        .sum();

                // 실패한 날 확인
                if (dayDistance < challenge.getChallengeDistance().getDistance()) {
                    // 실패한 날의 이전까지의 성공 횟수와 거리 계산
                    int successDays = countSuccessDaysBeforeFailure(mate.getId(), challenge, day - 1);
                    double totalDistance = calculateTotalDistanceBeforeFailure(mate.getId(), challenge, day - 1);

                    return new SoloChallengeResponse.ChallengeMateInfo(
                            mate.getNickname(),
                            mate.getTendency(),
                            successDays,
                            false, // 실패 상태이므로 항상 false
                            totalDistance
                    );
                }
            }
        }

        // IN_PROGRESS 상태인 경우 현재 일차의 정보 반환
        // 현재 일차의 시작시간과 종료시간 계산
        LocalDateTime dayStart = challenge.getStartDate().plusDays(currentDay - 1);
        LocalDateTime dayEnd = currentDay == calculateDayFromStart(challenge.getStartDate(), now)
                ? now
                : challenge.getStartDate().plusDays(currentDay);

        // 현재 일차의 러닝 거리 계산
        double currentDayDistance = runningDataRepository
                .findAllByUserIdAndStartTimeBetweenAndStatus(
                        mate.getId(),
                        dayStart,
                        dayEnd,
                        RunningDataStatus.ACTIVE.name()
                )
                .stream()
                .mapToDouble(data -> data.getTotalDistance() / 1000.0)
                .sum();

        // 이전 일차까지의 성공 횟수 계산
        int successDays = countSuccessDaysBeforeFailure(mate.getId(), challenge, currentDay - 1);

        // 현재 일차 성공 여부 확인
        boolean challengeMateIsSuccess = currentDayDistance >= challenge.getChallengeDistance().getDistance();
        if (challengeMateIsSuccess) {
            successDays++;
        }

        return new SoloChallengeResponse.ChallengeMateInfo(
                mate.getNickname(),
                mate.getTendency(),
                successDays,
                challengeMateIsSuccess,
                currentDayDistance
        );
    }

    // 6. 러닝 후 솔로 챌린지 결과 조회
    public SoloChallengeResponse.SoloChallengeRunningResultRes getSoloChallengeRunningResult (String accessToken) {

        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 유저가 참여 중인 챌린지 조회
        UserSoloChallenge userSoloChallenge = userSoloChallengeRepository
                .findFirstByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));
        SoloChallenge soloChallenge = userSoloChallenge.getSoloChallenge();

        // 챌린지 메이트 조회
        Long mateId = userSoloChallengeRepository
                .findBySoloChallengeIdAndUserIdNot(soloChallenge.getId(), user.getId())
                .map(mate -> mate.getUser().getId())
                .orElse(null);

        Optional<User> challengeMate = userRepository.findById(mateId);

        // 챌린지 메이트의 거리 및 정보 조회
        SoloChallengeResponse.ChallengeMateInfo mateInfo = getChallengeMateInfo(challengeMate.get(), soloChallenge);

        // 유저의 금일 성공 여부
        boolean userIsSuccess = checkTodaySuccess(user.getId(), soloChallenge);

        return new SoloChallengeResponse.SoloChallengeRunningResultRes(soloChallenge.getChallengePeriod().getDays(),
                soloChallenge.getChallengeDistance().getDistance(),
                calculateDayCount(soloChallenge.getStartDate()),
                mateInfo,
                userIsSuccess,
                user.getTendency()
        );

    }

    // 7. 솔로 챌린지에 참여하기
    @Transactional
    public SoloChallengeResponse.SoloChallengeMateRes joinSoloChallenge(Long challengeId, String accessToken) {

        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

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
                user.getId(), Arrays.asList(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS))) {

            // 5. 본인 챌린지 참여 방지
            if (creatorChallenge.getUser().getId().equals(user.getId())) {
                throw new ChallengeException(ErrorCode.CANNOT_JOIN_OWN_CHALLENGE);
            } else {
                throw new ChallengeException(ErrorCode.INVALID_CHALLENGE_JOIN);
            }

        }

        // 6. UserSoloChallenge 생성 및 저장
        UserSoloChallenge userSoloChallenge = ChallengeConverter.toUserSoloChallenge(
                User.builder().id(user.getId()).build(),
                soloChallenge,
                false);
        userSoloChallengeRepository.save(userSoloChallenge);

        // 8. 챌린지 상태 업데이트
        soloChallenge.updateStatus(ChallengeStatus.IN_PROGRESS);

        return new SoloChallengeResponse.SoloChallengeMateRes(challengeId, creatorChallenge.getUser().getId());
    }

    // 홈 화면에서 유저의 챌린지 관련 화면 조회
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public SoloChallengeResponse.HomeChallengeRes getUserChallenges(String accessToken) {

        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 솔로 챌린지 조회
        UserSoloChallenge userSoloChallenge = userSoloChallengeRepository
                .findFirstByUserIdAndSoloChallenge_ChallengeStatusInOrderByCreatedAtDesc(
                        user.getId(),
                        List.of(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS))
                .orElse(null);

        // 크루 챌린지 조회 (4명이 결성된 크루일 경우에만)
        UserCrewChallenge userCrewChallenge = userCrewChallengeRepository
                .findFirstByUserIdAndCrewChallenge_ChallengeStatusInOrderByCreatedAtDesc(
                        user.getId(),
                        List.of(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS)
                ).orElse(null);

        // 응답 DTO 생성
        SoloChallengeResponse.UserSoloChallengeInfo soloInfo = null;
        if (userSoloChallenge != null) {
            SoloChallenge challenge = userSoloChallenge.getSoloChallenge();
            int soloCountDay = calculateCountDay(challenge.getStartDate().toLocalDate());

            // 챌린지 메이트 조회
            Long mateId = null;
            String mateNickname = "";
            Tendency tendency = null;
            LocalDateTime soloStartDate = userSoloChallenge.getSoloChallenge().getStartDate();

            if (challenge.getChallengeStatus() == ChallengeStatus.IN_PROGRESS) {
                mateId = userSoloChallengeRepository
                        .findBySoloChallengeIdAndUserIdNot(challenge.getId(), user.getId())
                        .map(mate -> mate.getUser().getId())
                        .orElse(null);

                Optional<User> challengeMate = userRepository.findById(mateId);

                mateNickname = userRepository.findNicknameById(challengeMate.get().getId())
                        .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

                tendency = userRepository.findTendencyById(user.getId())
                        .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

            }

            soloInfo = ChallengeConverter.toUserSoloChallengeInfo(challenge, user, mateId, mateNickname, tendency, soloCountDay, formatDateTime(soloStartDate));
        }

        SoloChallengeResponse.UserCrewChallengeInfo crewInfo = null;
        if (userCrewChallenge != null) {
            CrewChallenge challenge = userCrewChallenge.getCrewChallenge();
            LocalDateTime crewStartDate = challenge.getStartDate();
            int crewCountDay = calculateCountDay(challenge.getStartDate().toLocalDate());

            // 크루원 수 확인
            long memberCount = userCrewChallengeRepository.countByCrewChallengeId(challenge.getId());
            if (memberCount == 4) {  // 4명이 모인 크루만 응답
                List<CrewChallengeResponse.MemberTendencyInfo> participantInfos = crewChallengeService.getMemberTendencyInfos(challenge.getId());
                crewInfo = ChallengeConverter.toUserCrewChallengeInfo(challenge, participantInfos, crewCountDay, formatDateTime(crewStartDate));
            }
        }

        return new SoloChallengeResponse.HomeChallengeRes(soloInfo, crewInfo);
    }

    // 챌린지 매칭 확인 응답
    public SoloChallengeResponse.CheckChallengeMatchingRes getCheckChallengeMatching(String accessToken) {
        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 솔로 챌린지 매칭 확인
        boolean isSoloChallengeMatching = userSoloChallengeRepository
                .findFirstByUserIdAndSoloChallenge_ChallengeStatusInOrderByCreatedAtDesc(
                        user.getId(),
                        List.of(ChallengeStatus.IN_PROGRESS)
                ).isPresent();

        // 크루 챌린지 매칭 확인
        boolean isCrewChallengeMatching = userCrewChallengeRepository
                .findFirstByUserIdAndCrewChallenge_ChallengeStatusInOrderByCreatedAtDesc(
                        user.getId(),
                        List.of(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS)
                ).isPresent();

        return new SoloChallengeResponse.CheckChallengeMatchingRes(
                isSoloChallengeMatching,
                isCrewChallengeMatching
        );
    }

    // 활용 메소드들
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

    // 포맷터를 상수로 정의
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // String -> LocalDateTime 변환
    private LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
    }

    // LocalDateTime -> String 변환
    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    // 날짜만을 위한 포맷터 상수 정의
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // LocalDateTime -> 날짜만 String으로 변환
    private String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }

    // 일자별 성공 여부 조회
    private boolean checkTodaySuccess(Long userId, SoloChallenge challenge) {
        LocalDateTime now = LocalDateTime.now();
        int currentDay = calculateDayFromStart(challenge.getStartDate(), now);

        LocalDateTime dayStart = challenge.getStartDate().plusDays(currentDay - 1);

        return runningDataRepository
                .findAllByUserIdAndStartTimeBetweenAndStatus(
                        userId,
                        dayStart,
                        now,
                        RunningDataStatus.ACTIVE.name()
                )
                .stream()
                .mapToDouble(RunningData::getTotalDistance)
                .sum() >= challenge.getChallengeDistance().getDistance() * 1000; // m 단위로 변환
    }

    // 시작일로 부터 며칠 째인지 (일자별 달린 거리 조회용)
    private int calculateDayFromStart(LocalDateTime startDate, LocalDateTime currentDate) {
        // 시작 시간부터 현재까지의 시간 차이를 계산
        long hoursBetween = ChronoUnit.HOURS.between(startDate, currentDate);
        // 24시간을 기준으로 일수 계산 (올림 처리)
        return (int) Math.ceil((double) hoursBetween / 24);
    }

    // 며칠 째인지 조회
    private int calculateDayCount(LocalDateTime startDate) {
        return (int) ChronoUnit.DAYS.between(startDate.toLocalDate(), LocalDate.now()) + 1;
    }

    // 실패 이전까지의 성공 일수 계산
    private int countSuccessDaysBeforeFailure(Long userId, SoloChallenge challenge, int lastDay) {
        int successCount = 0;
        for (int day = 1; day <= lastDay; day++) {
            LocalDateTime dayStart = challenge.getStartDate().plusDays(day - 1);
            LocalDateTime dayEnd = challenge.getStartDate().plusDays(day);

            double dayDistance = runningDataRepository
                    .findAllByUserIdAndStartTimeBetweenAndStatus(
                            userId,
                            dayStart,
                            dayEnd,
                            RunningDataStatus.ACTIVE.name()
                    )
                    .stream()
                    .mapToDouble(data -> data.getTotalDistance() / 1000.0)
                    .sum();

            if (dayDistance >= challenge.getChallengeDistance().getDistance()) {
                successCount++;
            }
        }
        return successCount;
    }

    // 실패 이전까지의 총 거리 계산
    private double calculateTotalDistanceBeforeFailure(Long userId, SoloChallenge challenge, int lastDay) {
        LocalDateTime dayStart = challenge.getStartDate().plusDays(lastDay - 1);
        LocalDateTime dayEnd = challenge.getStartDate().plusDays(lastDay);

        return runningDataRepository
                .findAllByUserIdAndStartTimeBetweenAndStatus(
                        userId,
                        dayStart,
                        dayEnd,
                        RunningDataStatus.ACTIVE.name()
                )
                .stream()
                .mapToDouble(data -> data.getTotalDistance() / 1000.0)
                .sum();
    }

    // 1분 마다 솔로 챌린지 성공 여부 확인
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkSoloChallengeStatus() {
        LocalDateTime now = LocalDateTime.now();

        // 현재 진행중이고, 시작된 챌린지만 조회하도록 조건 추가
        List<UserSoloChallenge> inProgressChallenges = userSoloChallengeRepository
                .findAllByChallengeResultAndSoloChallenge_StartDateLessThanEqual(
                        ChallengeResult.IN_PROGRESS,
                        now
                );

        for (UserSoloChallenge userChallenge : inProgressChallenges) {
            try {
                SoloChallenge challenge = userChallenge.getSoloChallenge();

                // 이미 종료된 챌린지는 처리하지 않음
                if (challenge.getEndDate().isBefore(now)) {
                    continue;
                }

                int currentDay = calculateDayFromStart(challenge.getStartDate(), now);
                boolean failureFound = false;

                // 이전 일차의 성공 여부 확인 (현재 일차 제외 : 금일까지는 IN_PROGRESS)
                for (int day = 1; day < currentDay; day++) {
                    LocalDateTime dayStart = challenge.getStartDate().plusDays(day - 1);
                    LocalDateTime dayEnd = challenge.getStartDate().plusDays(day);

                    double dayDistance = runningDataRepository
                            .findAllByUserIdAndStartTimeBetweenAndStatus(
                                    userChallenge.getUser().getId(),
                                    dayStart,
                                    dayEnd,
                                    RunningDataStatus.ACTIVE.name()
                            )
                            .stream()
                            .mapToDouble(data -> data.getTotalDistance() / 1000.0)
                            .sum();

                    if (dayDistance < challenge.getChallengeDistance().getDistance()) {
                        userChallenge.updateChallengeResult(ChallengeResult.FAILURE);
                        userSoloChallengeRepository.save(userChallenge);
                        failureFound = true;
                        break;
                    }
                }

                if (!failureFound) {
                    // 현재까지 실패가 없다면 IN_PROGRESS 유지
                    userChallenge.updateChallengeResult(ChallengeResult.IN_PROGRESS);
                    userSoloChallengeRepository.save(userChallenge);
                }
            } catch (Exception e) {
                log.error("Error processing challenge for user {}: {}",
                        userChallenge.getUser().getId(), e.getMessage());
            }
        }
    }
}
