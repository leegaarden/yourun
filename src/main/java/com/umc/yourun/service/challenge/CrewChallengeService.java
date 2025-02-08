package com.umc.yourun.service.challenge;

import com.umc.yourun.config.JwtTokenProvider;
import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.GeneralException;
import com.umc.yourun.config.exception.custom.ChallengeException;
import com.umc.yourun.converter.ChallengeConverter;
import com.umc.yourun.domain.CrewChallenge;
import com.umc.yourun.domain.RunningData;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.ChallengePeriod;
import com.umc.yourun.domain.enums.ChallengeStatus;
import com.umc.yourun.domain.enums.RunningDataStatus;
import com.umc.yourun.domain.enums.Tendency;
import com.umc.yourun.domain.mapping.UserCrewChallenge;
import com.umc.yourun.dto.challenge.ChallengeRequest;
import com.umc.yourun.dto.challenge.CrewChallengeResponse;
import com.umc.yourun.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewChallengeService {

    private final CrewChallengeRepository crewChallengeRepository;
    private final UserCrewChallengeRepository userCrewChallengeRepository;
    private final RunningDataRepository runningDataRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 1. 크루 챌린지 생성 및 응답
    @Transactional
    public CrewChallengeResponse.CrewChallengeCreateRes createCrewChallenge(ChallengeRequest.CreateCrewChallengeReq request, String accessToken) {

        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 현재 시간 기준으로 시작 시간 설정 (다음날 같은 시간) 및 종료 시간
        LocalDateTime startDateTime = parseDateTime(formatDateTime(LocalDateTime.now().plusDays(1)));
        // 종료일에 현재 시간 결합
        LocalDateTime endDateTime = parseDateTime(formatDateTime(LocalDateTime.of(
                request.endDate(),
                startDateTime.toLocalTime()
        )));


        // 이미 진행 중 (혹은 대기) 인 크루 챌린지가 있는지 검사
        if (userCrewChallengeRepository.existsByUserIdAndCrewChallenge_ChallengeStatusIn(
                user.getId(),
                Arrays.asList(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS))) {

            UserCrewChallenge userCrewChallenge = userCrewChallengeRepository
                    .findFirstByUserIdOrderByCreatedAtDesc(user.getId())
                    .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

            // 사용자가 생성자였던 경우
            if (userCrewChallenge.isCreator()) {
                throw new ChallengeException(ErrorCode.INVALID_CHALLENGE_CREATE);
            } else {
                throw new ChallengeException(ErrorCode.INVALID_CHALLENGE_JOIN);
            }

        }

        // 날짜 검사 및 기간 반환
        ChallengePeriod period = validateDates(request.endDate());

        CrewChallenge crewChallenge = ChallengeConverter.toCrewChallenge(request, startDateTime, endDateTime, period);
        CrewChallenge savedCrewChallenge = crewChallengeRepository.save(crewChallenge);

        UserCrewChallenge userCrewChallenge = ChallengeConverter.toUserCrewChallenge(user, savedCrewChallenge, true);
        userCrewChallengeRepository.save(userCrewChallenge);

        return new CrewChallengeResponse.CrewChallengeCreateRes(savedCrewChallenge.getId(),
                savedCrewChallenge.getCrewName(), savedCrewChallenge.getSlogan(),
                formatDateTime(startDateTime), formatDateTime(endDateTime),
                savedCrewChallenge.getChallengePeriod().getDays(), user.getTendency());
    }

    // 2. PENDING 상태인 크루 챌린지 조회 (화면)
    @Transactional(readOnly = true)
    public CrewChallengeResponse.CrewChallenge getPendingCrewChallenges(String accessToken) {
        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 4명 결성 대기 중인 크루 챌린지들
        List<CrewChallengeResponse.CrewChallengeRes> pendingCrewChallenges = getPendingCrewChallenge();

        return new CrewChallengeResponse.CrewChallenge(user.getId(), user.getTendency(),
                user.getCrewReward(), user.getPersonalReward(), pendingCrewChallenges);

    }

    // 2-1. PENDING 상태인 크루 챌린지
    @Transactional(readOnly = true)
    public List<CrewChallengeResponse.CrewChallengeRes> getPendingCrewChallenge() {

        List<CrewChallenge> pendingChallenges = crewChallengeRepository.findRandomPendingChallenges(5);

        return pendingChallenges.stream()
                .map(challenge -> {
                    // 현재 참여자들의 ID와 성향 정보 조회
                    List<CrewChallengeResponse.MemberTendencyInfo> participantInfos = getMemberTendencyInfos(challenge.getId());
                    if (participantInfos.size() >= 4) {
                        return null;
                    }

                    int remaining = 4 - participantInfos.size();

                    int reward = switch (challenge.getChallengePeriod().getDays()) {
                        case 3 -> 1;
                        case 4 -> 2;
                        case 5 -> 3;
                        default -> 0;
                    };

                    return new CrewChallengeResponse.CrewChallengeRes(
                            challenge.getId(),
                            challenge.getCrewName(),
                            challenge.getChallengePeriod().getDays(),
                            remaining,
                            reward,
                            participantInfos
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 3. 크루 챌린지 상세 조회
    @Transactional(readOnly = true)
    public CrewChallengeResponse.CrewChallengeDetailRes getCrewChallengeDetail(Long challengeId, String accessToken) {
        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 크루 챌린지 조회
        Optional<CrewChallenge> crewChallenge = crewChallengeRepository.findById(challengeId);

        List<Long> participants = userCrewChallengeRepository
                .findByCrewChallengeIdOrderByCreatedAt(challengeId)
                .stream()
                .map(uc -> uc.getUser().getId())
                .toList();

        List<CrewChallengeResponse.MemberTendencyInfo> participantInfos = getMemberTendencyInfos(challengeId);
        // 기간에 따른 보상 계산
        int reward = switch (crewChallenge.get().getChallengePeriod().getDays()) {
            case 3 -> 1;
            case 4 -> 2;
            case 5 -> 3;
            default -> 0;
        };

        return new CrewChallengeResponse.CrewChallengeDetailRes(crewChallenge.get().getCrewName(),
                formatDateTime(crewChallenge.get().getStartDate()), formatDateTime(crewChallenge.get().getEndDate()),
                crewChallenge.get().getChallengePeriod().getDays(), participants.size(), reward, participantInfos, crewChallenge.get().getSlogan());

    }

    // 4. 크루 챌린지 매칭 화면
    @Transactional
    public CrewChallengeResponse.CrewChallengeMatchingRes getCrewMatch(String accessToken) {

        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 1. 사용자의 현재 크루 챌린지 참여 정보 조회
        UserCrewChallenge userCrewChallenge = userCrewChallengeRepository
                .findFirstByUserIdAndCrewChallenge_ChallengeStatusInOrderByCreatedAtDesc(
                        user.getId(),
                        List.of(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS)
                ).orElseThrow(() -> new ChallengeException(ErrorCode.NO_CREW_CHALLENGE_FOUND));

        if (userCrewChallenge == null) {
            throw new GeneralException(ErrorCode.NO_CREW_CHALLENGE_FOUND);
        }

        // 2. 내 크루원 ID 목록 조회 (참여 순서대로)
        CrewChallenge myCrew = userCrewChallenge.getCrewChallenge();

        List<CrewChallengeResponse.MemberTendencyInfo> myParticipantIdsInfo = getMemberTendencyInfos(userCrewChallenge.getCrewChallenge().getId());

        // 3. 매칭된 크루 정보 조회
        String matchedCrewName = null;
        String matchedCrewSlogan = null;
        List<CrewChallengeResponse.MemberTendencyInfo> matchedParticipantIdsInfo = new ArrayList<>();

        if (myCrew.getMatchedCrewChallengeId() != null) {
            CrewChallenge matchedCrew = crewChallengeRepository
                    .findById(myCrew.getMatchedCrewChallengeId())
                    .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

            matchedCrewName = matchedCrew.getCrewName();
            matchedCrewSlogan = matchedCrew.getSlogan();
            matchedParticipantIdsInfo = getMemberTendencyInfos(matchedCrew.getId());

        }

        return new CrewChallengeResponse.CrewChallengeMatchingRes(
                myCrew.getChallengePeriod().getDays(),
                myCrew.getCrewName(),
                myCrew.getSlogan(),
                myParticipantIdsInfo,
                matchedCrewName,
                matchedCrewSlogan,
                matchedParticipantIdsInfo);

    }

    // 5. 크루 챌린지의 상세 진행도 (홈 화면 - 크루 챌린지 클릭)
    public CrewChallengeResponse.CrewChallengeDetailProgressRes getCrewChallengeDetailProgress(String accessToken) {

        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 1. 유저가 참여 중인 크루 챌린지
        Long challengeId =userCrewChallengeRepository
                .findFirstByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND))
                .getCrewChallenge().getId();
        CrewChallenge myCrew = crewChallengeRepository.findById(challengeId)
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

        int challengePeriod = myCrew.getChallengePeriod().getDays();
        String crewName = myCrew.getCrewName();

        // 2. 유저가 속한 크루의 크루원들 정보
        List<CrewChallengeResponse.CrewMemberInfo> myCrewMembers = getCrewMemberInfo(user.getId(), challengeId);

        // 3. 매칭된 크루 정보 조회
        CrewChallenge matchedCrew = crewChallengeRepository.findById(myCrew.getMatchedCrewChallengeId())
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

        String matchedCrewName = matchedCrew.getCrewName();
        Optional<UserCrewChallenge> matchedCrewCreator = userCrewChallengeRepository.findByCrewChallengeIdAndIsCreator(matchedCrew.getId(), true);

        // 4. 유저 크루와 매칭된 크루의 거리
        List<Long> matchedCrewMemberIds = userCrewChallengeRepository
                .findByCrewChallengeIdOrderByCreatedAt(matchedCrew.getId())
                .stream()
                .map(uc -> uc.getUser().getId())
                .toList();

        double myCrewDistance = myCrewMembers.stream()
                .mapToDouble(CrewChallengeResponse.CrewMemberInfo::runningDistance)
                .sum();
        double matchedCrewDistance = matchedCrewMemberIds.stream()
                .mapToDouble(memberId -> calculateTotalDistance(myCrew.getMatchedCrewChallengeId(), memberId))
                .sum();

        return new CrewChallengeResponse.CrewChallengeDetailProgressRes(challengePeriod, crewName, myCrew.getSlogan(), myCrewMembers,
                myCrewDistance, matchedCrewName, matchedCrew.getSlogan(),
                matchedCrewCreator.get().getUser().getTendency(), matchedCrewDistance, formatDateTime(LocalDateTime.now()));

    }

    // 6. 러닝 후 크루 챌린지 결과 확인
    @Transactional(readOnly = true)
    public CrewChallengeResponse.CrewChallengeRunningResultRes getCrewChallengeRunningResult (String accessToken) {

        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 1. 유저의 현재 크루 챌린지 조회
        UserCrewChallenge userCrewChallenge = userCrewChallengeRepository
                .findFirstByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));
        CrewChallenge myCrewChallenge = userCrewChallenge.getCrewChallenge();

        // 2. 유저가 속한 크루의 거리
        List<CrewChallengeResponse.CrewMemberInfo> myCrewMembers = getCrewMemberInfo(user.getId(), myCrewChallenge.getId());

        // 유저가 방금 뛴 거리
        Optional<RunningData> latestRunning = runningDataRepository.findTopByUserIdAndStatusOrderByCreatedAtDesc(user.getId(), RunningDataStatus.ACTIVE);
        double userDistance = latestRunning.get().getTotalDistance() / 1000.0;
        double afterDistance = myCrewMembers.stream()
                .mapToDouble(CrewChallengeResponse.CrewMemberInfo::runningDistance)
                .sum();
        double beforeDistance = afterDistance - userDistance;

        // 3. 매칭된 크루 정보 조회
        CrewChallenge matchedCrewChallenge = crewChallengeRepository.findById(myCrewChallenge.getMatchedCrewChallengeId())
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

        String matchedCrewName = matchedCrewChallenge.getCrewName();
        Tendency matchedCrewCreator = userCrewChallengeRepository.findByCrewChallengeIdAndIsCreator(matchedCrewChallenge.getId(), true)
                .get().getUser().getTendency();

        // 상태 검증 (두 크루 챌린지가 모두 종료가 안 됐을 경우)
        if (myCrewChallenge.getChallengeStatus() != ChallengeStatus.COMPLETED
                || matchedCrewChallenge.getChallengeStatus() != ChallengeStatus.COMPLETED) {
            throw new ChallengeException(ErrorCode.CREW_CHALLENGE_COMPLETED);
        }

        // 4. 유저 크루와 매칭된 크루의 거리
        List<Long> matchedCrewMemberIds = userCrewChallengeRepository
                .findByCrewChallengeIdOrderByCreatedAt(matchedCrewChallenge.getId())
                .stream()
                .map(uc -> uc.getUser().getId())
                .toList();

        double matchedCrewDistance = matchedCrewMemberIds.stream()
                .mapToDouble(memberId -> calculateTotalDistance(myCrewChallenge.getMatchedCrewChallengeId(), memberId))
                .sum();

        return new CrewChallengeResponse.CrewChallengeRunningResultRes(myCrewChallenge.getChallengePeriod().getDays(),
                myCrewChallenge.getCrewName(), beforeDistance, userDistance, afterDistance, matchedCrewName,
                matchedCrewCreator, matchedCrewDistance);
    }

    // 7. 크루 챌린지 순위 결과 화면
    @Transactional(readOnly = true)
    public CrewChallengeResponse.CrewChallengeContributionRes getCrewChallengeContribution(String accessToken) {
        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 1. 유저의 현재 크루 챌린지 조회
        UserCrewChallenge userCrewChallenge = userCrewChallengeRepository
                .findFirstByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));
        CrewChallenge crewChallenge = userCrewChallenge.getCrewChallenge();

        // 2. 크루원들과 거리 정보 조회
        List<UserCrewChallenge> crewMembers = userCrewChallengeRepository.findByCrewChallengeIdOrderByCreatedAt(crewChallenge.getId());

        // 3. 거리 정보와 순위를 포함한 크루원 정보 리스트 생성
        List<CrewChallengeResponse.CrewMemberRankingInfo> crewMemberRankings = new ArrayList<>();

        // 먼저 모든 거리 정보 수집 및 정렬
        List<Double> sortedDistances = crewMembers.stream()
                .map(member -> calculateTotalDistance(crewChallenge.getId(), member.getUser().getId()))
                .sorted(Comparator.reverseOrder())
                .distinct()
                .toList();

        // 유저를 먼저 처리하고, 나머지는 참여 순서대로
        for (UserCrewChallenge member : crewMembers) {
            double distance = calculateTotalDistance(crewChallenge.getId(), member.getUser().getId());
            int rank = sortedDistances.indexOf(distance) + 1;

            CrewChallengeResponse.CrewMemberRankingInfo rankingInfo = new CrewChallengeResponse.CrewMemberRankingInfo(
                    member.getUser().getId(),
                    distance,
                    member.getUser().getTendency(),
                    rank
            );

            // 현재 유저의 정보는 리스트의 맨 앞으로
            if (member.getUser().getId().equals(user.getId())) {
                crewMemberRankings.add(0, rankingInfo);
            } else {
                crewMemberRankings.add(rankingInfo);
            }
        }

        // 4. MVP 선정 (1등)
        Long mvpId = crewMemberRankings.stream()
                .filter(member -> member.rank() == 1)
                .findFirst()
                .map(CrewChallengeResponse.CrewMemberRankingInfo::userId)
                .orElse(null);

        // 5. 보상 계산
        int reward = switch (crewChallenge.getChallengePeriod().getDays()) {
            case 3 -> 1;
            case 4 -> 2;
            case 5 -> 3;
            default -> 0;
        };

        // 6. 승패 여부 계산
        double myCrewTotalDistance = crewMemberRankings.stream()
                .mapToDouble(CrewChallengeResponse.CrewMemberRankingInfo::runningDistance)
                .sum();

        CrewChallenge matchedCrew = crewChallengeRepository.findById(crewChallenge.getMatchedCrewChallengeId())
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

        double matchedCrewTotalDistance = userCrewChallengeRepository
                .findByCrewChallengeIdOrderByCreatedAt(matchedCrew.getId())
                .stream()
                .mapToDouble(member -> calculateTotalDistance(matchedCrew.getId(), member.getUser().getId()))
                .sum();

        boolean win = myCrewTotalDistance >= matchedCrewTotalDistance;

        return new CrewChallengeResponse.CrewChallengeContributionRes(
                crewChallenge.getChallengePeriod().getDays(),
                reward,
                crewChallenge.getCrewName(),
                crewMemberRankings,
                mvpId,
                win
        );
    }

    // 8. 크루 챌린지에 참여하기
    @Transactional
    public CrewChallengeResponse.CrewChallengeMateRes joinCrewChallenge(Long challengeId, String accessToken) {
        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

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
        Optional<UserCrewChallenge> existUserCrewChallenge = userCrewChallengeRepository
                .findFirstByUserIdAndCrewChallenge_ChallengeStatusInOrderByCreatedAtDesc(
                        user.getId(),
                        List.of(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS)
                );

        UserCrewChallenge creator = userCrewChallengeRepository
                .findByCrewChallengeIdAndIsCreator(challengeId, true)
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

        if (existUserCrewChallenge.isPresent()) {
            throw new ChallengeException(ErrorCode.INVALID_CHALLENGE_JOIN);
        } else if (creator.getUser().getId().equals(user.getId())) {
            throw new ChallengeException(ErrorCode.CANNOT_JOIN_OWN_CHALLENGE);

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

        return new CrewChallengeResponse.CrewChallengeMateRes(challengeId, participants);
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

    // 크루 챌린지 중 특정 유저의 달린 거리 계산
    private double calculateTotalDistance(Long challengeId, Long userId) {
        CrewChallenge challenge = crewChallengeRepository.findById(challengeId)
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

        LocalDateTime periodStart = challenge.getStartDate();
        LocalDateTime currentTime = LocalDateTime.now();

        if (currentTime.isBefore(periodStart)) {
            return 0.0;
        }

        // 미터 단위 합계를 킬로미터로 변환
        return runningDataRepository.sumDistanceByUserIdAndPeriod(userId, periodStart, currentTime) / 1000.0;
    }

    // 챌린지 참여자 정보와 성향 조회
    public List<CrewChallengeResponse.MemberTendencyInfo> getMemberTendencyInfos(Long challengeId) {
        return userCrewChallengeRepository
                .findByCrewChallengeIdOrderByCreatedAt(challengeId)
                .stream()
                .map(uc -> new CrewChallengeResponse.MemberTendencyInfo(
                        uc.getUser().getId(),
                        uc.getUser().getTendency()
                ))
                .collect(Collectors.toList());
    }

    // 참여자 정보 및 거리 조회
    private List<CrewChallengeResponse.CrewMemberInfo> getCrewMemberInfo (Long userId, Long challengeId) {
        return userCrewChallengeRepository
                .findByCrewChallengeIdOrderByCreatedAt(challengeId)
                .stream()
                .map(member -> new CrewChallengeResponse.CrewMemberInfo(
                        member.getUser().getId(),
                        calculateTotalDistance(challengeId, member.getUser().getId()),
                        member.getUser().getTendency()
                ))
                .toList();
    }

    // 포맷터를 상수로 정의
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // String -> LocalDateTime 변환
    private LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
    }

    // LocalDateTime -> String 변환
    public String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    // 날짜만을 위한 포맷터 상수 정의
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // LocalDateTime -> 날짜만 String으로 변환
    private String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }
}
