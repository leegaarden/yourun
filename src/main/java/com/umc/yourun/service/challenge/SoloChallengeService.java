package com.umc.yourun.service.challenge;

import com.umc.yourun.config.JwtTokenProvider;
import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.GeneralException;
import com.umc.yourun.config.exception.custom.ChallengeException;
import com.umc.yourun.converter.ChallengeConverter;
import com.umc.yourun.domain.CrewChallenge;
import com.umc.yourun.domain.SoloChallenge;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.ChallengePeriod;
import com.umc.yourun.domain.enums.ChallengeStatus;
import com.umc.yourun.domain.enums.Tendency;
import com.umc.yourun.domain.mapping.UserCrewChallenge;
import com.umc.yourun.domain.mapping.UserSoloChallenge;
import com.umc.yourun.dto.challenge.ChallengeRequest;
import com.umc.yourun.dto.challenge.ChallengeResponse;
import com.umc.yourun.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SoloChallengeService {

    private final CrewChallengeRepository crewChallengeRepository;
    private final SoloChallengeRepository soloChallengeRepository;
    private final UserSoloChallengeRepository userSoloChallengeRepository;
    private final UserCrewChallengeRepository userCrewChallengeRepository;
    private final UserRepository userRepository;
    private final RunningDataRepository runningDataRepository;
    private final JwtTokenProvider jwtTokenProvider;


    // 1. 솔로 챌린지 생성
    @Transactional
    public ChallengeResponse.SoloChallengeCreate createSoloChallenge(ChallengeRequest.CreateSoloChallengeReq request, String accessToken) {

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

            UserSoloChallenge userSoloChallenge = userSoloChallengeRepository.findByUserId(user.getId());

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

        return new ChallengeResponse.SoloChallengeCreate(savedSoloChallenge.getId(),
                formatDateTime(startDateTime), formatDateTime(endDateTime),
                savedSoloChallenge.getChallengePeriod().getDays(), user.getTendency());
    }

    // 2. PENDING 상태인 솔로 챌린지 조회 (화면)
    @Transactional(readOnly = true)
    public ChallengeResponse.SoloChallenge getPendingSoloChallenges(String accessToken) {
        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);
        List<ChallengeResponse.SoloChallengeRes> pendingSoloChallenges = getPendingSoloChallenge();

        return new ChallengeResponse.SoloChallenge(user.getId(), user.getTendency(),
                user.getCrewReward(), user.getPersonalReward(), pendingSoloChallenges);
    }

    // 2-1. PENDING 상태인 솔로 챌린지
    @Transactional(readOnly = true)
    public List<ChallengeResponse.SoloChallengeRes> getPendingSoloChallenge() {


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

                    return new ChallengeResponse.SoloChallengeRes(
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
    public ChallengeResponse.SoloChallengeDetailRes getSoloChallengeDetail(Long challengeId, String accessToken) {

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

        return new ChallengeResponse.SoloChallengeDetailRes(formatDateTime(soloChallenge.get().getStartDate()),
                formatDateTime(soloChallenge.get().getEndDate()), soloChallenge.get().getChallengeDistance().getDistance(),
                soloChallenge.get().getChallengePeriod().getDays(), nickname, hashtags, creatorUser.getTendency(),
                reward, countDay);
    }

    // 4. 솔로 챌린지 매칭 화면
    @Transactional(readOnly = true)
    public ChallengeResponse.SoloChallengeMatchingRes getSoloChallengeMatching(String accessToken) {

        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 유저가 참여 중인 챌린지 조회
        UserSoloChallenge userSoloChallenge = userSoloChallengeRepository.findByUserId(user.getId());
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

        return new ChallengeResponse.SoloChallengeMatchingRes(soloChallenge.getChallengePeriod().getDays(),
                soloChallenge.getChallengeDistance().getDistance(), user.getTendency(),
                user.getNickname(), userCountDay, userHashtags, challengeMate.get().getTendency(),
                challengeMate.get().getNickname(), challengeMateCountDay, challengeHashtags);

    }

    // 7. 솔로 챌린지에 참여하기
    @Transactional
    public ChallengeResponse.SoloChallengeMateRes joinSoloChallenge(Long challengeId, String accessToken) {

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

        return new ChallengeResponse.SoloChallengeMateRes(challengeId, creatorChallenge.getUser().getId());
    }

    // 홈 화면에서 유저의 챌린지 관련 화면 조회
    @Transactional(readOnly = true)
    public ChallengeResponse.HomeChallengeRes getUserChallenges(String accessToken) {

        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 솔로 챌린지 조회
        UserSoloChallenge userSoloChallenge = userSoloChallengeRepository
                .findByUserIdAndSoloChallenge_ChallengeStatusIn(
                        user.getId(),
                        List.of(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS))
                .orElse(null);

        // 크루 챌린지 조회 (4명이 결성된 크루일 경우에만)
        UserCrewChallenge userCrewChallenge = userCrewChallengeRepository
                .findByUserIdAndCrewChallenge_ChallengeStatusIn(
                        user.getId(),
                        List.of(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS));

        // 응답 DTO 생성
        ChallengeResponse.UserSoloChallengeInfo soloInfo = null;
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

                mateNickname = userRepository.findNicknameById(user.getId())
                        .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

                tendency = userRepository.findTendencyById(user.getId())
                        .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

            }

            soloInfo = ChallengeConverter.toUserSoloChallengeInfo(challenge, user, mateId, mateNickname, tendency, soloCountDay, formatDateTime(soloStartDate));
        }

        ChallengeResponse.UserCrewChallengeInfo crewInfo = null;
        LocalDateTime crewStartDate = userCrewChallenge.getCrewChallenge().getStartDate();
        if (userCrewChallenge != null) {
            CrewChallenge challenge = userCrewChallenge.getCrewChallenge();
            int crewCountDay = calculateCountDay(challenge.getStartDate().toLocalDate());

            // 크루원 수 확인
            long memberCount = userCrewChallengeRepository.countByCrewChallengeId(challenge.getId());
            if (memberCount == 4) {  // 4명이 모인 크루만 응답
                List<ChallengeResponse.MemberTendencyInfo> participantInfos = getMemberTendencyInfos(challenge.getId());

                crewInfo = ChallengeConverter.toUserCrewChallengeInfo(challenge, participantInfos, crewCountDay, formatDateTime(crewStartDate));
            }
        }

        return new ChallengeResponse.HomeChallengeRes(soloInfo, crewInfo);
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
    private List<ChallengeResponse.MemberTendencyInfo> getMemberTendencyInfos(Long challengeId) {
        return userCrewChallengeRepository
                .findByCrewChallengeIdOrderByCreatedAt(challengeId)
                .stream()
                .map(uc -> new ChallengeResponse.MemberTendencyInfo(
                        uc.getUser().getId(),
                        uc.getUser().getTendency()
                ))
                .collect(Collectors.toList());
    }

    // 참여자 정보 및 거리 조회
    private List<ChallengeResponse.CrewMemberInfo> getCrewMemberInfo (Long userId, Long challengeId) {
        return userCrewChallengeRepository
                .findByCrewChallengeIdOrderByCreatedAt(challengeId)
                .stream()
                .map(member -> new ChallengeResponse.CrewMemberInfo(
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
    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    // 날짜만을 위한 포맷터 상수 정의
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // LocalDateTime -> 날짜만 String으로 변환
    private String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }
}
