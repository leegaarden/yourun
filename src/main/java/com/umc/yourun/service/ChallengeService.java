package com.umc.yourun.service;

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
import jakarta.validation.constraints.Null;
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
public class ChallengeService {

    private final CrewChallengeRepository crewChallengeRepository;
    private final SoloChallengeRepository soloChallengeRepository;
    private final UserSoloChallengeRepository userSoloChallengeRepository;
    private final UserCrewChallengeRepository userCrewChallengeRepository;
    private final UserRepository userRepository;
    private final RunningDataRepository runningDataRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 크루 챌린지 생성 및 응답
    @Transactional
    public ChallengeResponse.CrewChallengeCreate createCrewChallenge(ChallengeRequest.CreateCrewChallengeReq request, String accessToken) {

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

            UserCrewChallenge userCrewChallenge = userCrewChallengeRepository.findByUserId(user.getId());

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

        return new ChallengeResponse.CrewChallengeCreate(savedCrewChallenge.getId(),
                savedCrewChallenge.getCrewName(), savedCrewChallenge.getSlogan(),
                formatDate(startDateTime), formatDate(endDateTime),
                savedCrewChallenge.getChallengePeriod().getDays(), user.getTendency());
    }

    // 솔로 챌린지 생성
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
                formatDate(startDateTime), formatDate(endDateTime),
                savedSoloChallenge.getChallengePeriod().getDays(), user.getTendency());
    }

    // PENDING 상태인 크루 챌린지 조회 : 크루원이 4명 미만으로 아직 결성되지 않은
    @Transactional(readOnly = true)
    public ChallengeResponse.CrewChallenge getPendingCrewChallenges(String accessToken) {
        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 4명 결성 대기 중인 크루 챌린지들
        List<ChallengeResponse.CrewChallengeRes> pendingCrewChallenges = getPendingCrewChallenge();

        return new ChallengeResponse.CrewChallenge(user.getId(), user.getTendency(),
                user.getCrewReward(), user.getPersonalReward(), pendingCrewChallenges);

    }

    @Transactional(readOnly = true)
    public List<ChallengeResponse.CrewChallengeRes> getPendingCrewChallenge() {

        List<CrewChallenge> pendingChallenges = crewChallengeRepository.findRandomPendingChallenges(5);

        return pendingChallenges.stream()
                .map(challenge -> {
                    // 현재 참여자들의 ID와 성향 정보 조회
                    List<ChallengeResponse.MemberTendencyInfo> participantInfos = getMemberTendencyInfos(challenge.getId());
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

                    return new ChallengeResponse.CrewChallengeRes(
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

    // PENDING 상태인 솔로 챌린지 조회
    @Transactional(readOnly = true)
    public ChallengeResponse.SoloChallenge getPendingSoloChallenges(String accessToken) {
        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);
        List<ChallengeResponse.SoloChallengeRes> pendingSoloChallenges = getPendingSoloChallenge();

        return new ChallengeResponse.SoloChallenge(user.getId(), user.getTendency(),
                user.getCrewReward(), user.getPersonalReward(), pendingSoloChallenges);
    }

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

    // 솔로 챌린지에 참여하기
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

    // 크루 챌린지에 참여하기
    @Transactional
    public ChallengeResponse.CrewChallengeMateRes joinCrewChallenge(Long challengeId, String accessToken) {
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
        UserCrewChallenge existUserCrewChallenge = userCrewChallengeRepository
                .findByUserIdAndCrewChallenge_ChallengeStatusIn(
                        user.getId(),
                        Arrays.asList(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS));

        UserCrewChallenge creator = userCrewChallengeRepository
                .findByCrewChallengeIdAndIsCreator(challengeId, true)
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

        if (existUserCrewChallenge != null) {

            if (creator.getUser().getId().equals(user.getId())) {
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
    @Transactional
    public ChallengeResponse.CrewChallengeMatchingRes getCrewMatch(String accessToken) {

        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 1. 사용자의 현재 크루 챌린지 참여 정보 조회
        UserCrewChallenge userCrewChallenge = userCrewChallengeRepository
                .findByUserIdAndCrewChallenge_ChallengeStatusIn(user.getId(),
                        List.of(ChallengeStatus.PENDING, ChallengeStatus.IN_PROGRESS));
        // .orElseThrow(() -> new ChallengeException(ErrorCode.NO_CREW_CHALLENGE_FOUND));

        if (userCrewChallenge == null) {
            throw new GeneralException(ErrorCode.NO_CREW_CHALLENGE_FOUND);
        }

        // 2. 내 크루원 ID 목록 조회 (참여 순서대로)
        CrewChallenge myCrew = userCrewChallenge.getCrewChallenge();

        List<ChallengeResponse.MemberTendencyInfo> myParticipantIdsInfo = getMemberTendencyInfos(userCrewChallenge.getCrewChallenge().getId());

        // 3. 매칭된 크루 정보 조회
        String matchedCrewName = null;
        String matchedCrewSlogan = null;
        List<ChallengeResponse.MemberTendencyInfo> matchedParticipantIdsInfo = new ArrayList<>();

        if (myCrew.getMatchedCrewChallengeId() != null) {
            CrewChallenge matchedCrew = crewChallengeRepository
                    .findById(myCrew.getMatchedCrewChallengeId())
                    .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

            matchedCrewName = matchedCrew.getCrewName();
            matchedCrewSlogan = matchedCrew.getSlogan();
            matchedParticipantIdsInfo = getMemberTendencyInfos(matchedCrew.getId());

        }

        return new ChallengeResponse.CrewChallengeMatchingRes(
                myCrew.getChallengePeriod().getDays(),
                myCrew.getCrewName(),
                myCrew.getSlogan(),
                myParticipantIdsInfo,
                matchedCrewName,
                matchedCrewSlogan,
                matchedParticipantIdsInfo);

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

            soloInfo = ChallengeConverter.toUserSoloChallengeInfo(challenge, user, mateId, mateNickname, tendency, soloCountDay, formatDate(soloStartDate));
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

                crewInfo = ChallengeConverter.toUserCrewChallengeInfo(challenge, participantInfos, crewCountDay, formatDate(crewStartDate));
            }
        }

        return new ChallengeResponse.HomeChallengeRes(soloInfo, crewInfo);
    }

    // 크루 챌린지의 상세 진행도 (홈 화면 - 크루 챌린지 클릭)
    public ChallengeResponse.CrewChallengeDetailProgressRes getCrewChallengeDetailProgress(String accessToken) {

        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 1. 유저가 참여 중인 크루 챌린지
        Long challengeId = userCrewChallengeRepository.findByUserId(user.getId()).getCrewChallenge().getId();
        CrewChallenge myCrew = crewChallengeRepository.findById(challengeId)
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

        int challengePeriod = myCrew.getChallengePeriod().getDays();
        String crewName = myCrew.getCrewName();

        // 2. 유저가 속한 크루의 크루원들 정보
        List<ChallengeResponse.CrewMemberInfo> myCrewMembers = sortCrewMembersWithUser(user.getId(), challengeId);

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
                .mapToDouble(ChallengeResponse.CrewMemberInfo::runningDistance)
                .sum();
        double matchedCrewDistance = matchedCrewMemberIds.stream()
                .mapToDouble(memberId -> calculateTotalDistance(myCrew.getMatchedCrewChallengeId(), memberId))
                .sum();

        boolean win = true;
        if (myCrewDistance >= matchedCrewDistance) {
            win = true;
        } else {
            win = false;
        }

        return new ChallengeResponse.CrewChallengeDetailProgressRes(challengePeriod, crewName, myCrew.getSlogan(), myCrewMembers,
                myCrewDistance, matchedCrewName, matchedCrew.getSlogan(),
                matchedCrewCreator.get().getUser().getTendency(), matchedCrewDistance, formatDateTime(LocalDateTime.now()), win);

    }

    // 크루 챌린지 상세 조회
    @Transactional(readOnly = true)
    public ChallengeResponse.CrewChallengeDetailRes getCrewChallengeDetail(Long challengeId, String accessToken) {
        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 크루 챌린지 조회
        Optional<CrewChallenge> crewChallenge = crewChallengeRepository.findById(challengeId);

        List<Long> participants = userCrewChallengeRepository
                .findByCrewChallengeIdOrderByCreatedAt(challengeId)
                .stream()
                .map(uc -> uc.getUser().getId())
                .toList();

        List<ChallengeResponse.MemberTendencyInfo> participantInfos = getMemberTendencyInfos(challengeId);
        // 기간에 따른 보상 계산
        int reward = switch (crewChallenge.get().getChallengePeriod().getDays()) {
            case 3 -> 1;
            case 4 -> 2;
            case 5 -> 3;
            default -> 0;
        };

        return new ChallengeResponse.CrewChallengeDetailRes(crewChallenge.get().getCrewName(),
                formatDate(crewChallenge.get().getStartDate()), formatDate(crewChallenge.get().getEndDate()),
                crewChallenge.get().getChallengePeriod().getDays(), participants.size(), reward, participantInfos, crewChallenge.get().getSlogan());

    }

    // 솔로 챌린지 상세 조회
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

        return new ChallengeResponse.SoloChallengeDetailRes(formatDate(soloChallenge.get().getStartDate()),
                formatDate(soloChallenge.get().getEndDate()), soloChallenge.get().getChallengeDistance().getDistance(),
                soloChallenge.get().getChallengePeriod().getDays(), nickname, hashtags, creatorUser.getTendency(),
                reward, countDay);
    }

    // 솔로 챌린지 매칭 화면
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

    // 크루 챌린지 기여도 결과 화면
    @Transactional(readOnly = true)
    public ChallengeResponse.CrewChallengeContributionRes getCrewChallengeContribution(String accessToken) {
        // 유저 조회
        User user = jwtTokenProvider.getUserByToken(accessToken);

        // 1. 유저의 현재 크루 챌린지 조회
        UserCrewChallenge userCrewChallenge = userCrewChallengeRepository.findByUserId(user.getId());
        CrewChallenge crewChallenge = userCrewChallenge.getCrewChallenge();

        // 2. 크루원들과 거리 정보 조회
        List<UserCrewChallenge> crewMembers = userCrewChallengeRepository.findByCrewChallengeIdOrderByCreatedAt(crewChallenge.getId());

        // 3. 거리 정보와 순위를 포함한 크루원 정보 리스트 생성
        List<ChallengeResponse.CrewMemberRankingInfo> crewMemberRankings = new ArrayList<>();

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

            ChallengeResponse.CrewMemberRankingInfo rankingInfo = new ChallengeResponse.CrewMemberRankingInfo(
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
                .map(ChallengeResponse.CrewMemberRankingInfo::userId)
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
                .mapToDouble(ChallengeResponse.CrewMemberRankingInfo::runningDistance)
                .sum();

        CrewChallenge matchedCrew = crewChallengeRepository.findById(crewChallenge.getMatchedCrewChallengeId())
                .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

        double matchedCrewTotalDistance = userCrewChallengeRepository
                .findByCrewChallengeIdOrderByCreatedAt(matchedCrew.getId())
                .stream()
                .mapToDouble(member -> calculateTotalDistance(matchedCrew.getId(), member.getUser().getId()))
                .sum();

        boolean win = myCrewTotalDistance > matchedCrewTotalDistance;

        return new ChallengeResponse.CrewChallengeContributionRes(
                crewChallenge.getChallengePeriod().getDays(),
                reward,
                crewChallenge.getCrewName(),
                crewMemberRankings,
                mvpId,
                win
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

    // 달린 거리 계산
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

    // 유저가 맨 앞으로, 이후로는 참여한 순서대로 (크루 챌린지 상세 진행도 및 결과 화면)
    private List<ChallengeResponse.CrewMemberInfo> sortCrewMembersWithUser(Long userId, Long challengeId) {
        return userCrewChallengeRepository
                .findByCrewChallengeIdOrderByCreatedAt(challengeId)
                .stream()
                .map(member -> new ChallengeResponse.CrewMemberInfo(
                        member.getUser().getId(),
                        calculateTotalDistance(challengeId, member.getUser().getId()),
                        member.getUser().getTendency()
                ))
                .sorted((m1, m2) -> {
                    if (m1.userId().equals(userId)) return -1;
                    if (m2.userId().equals(userId)) return 1;
                    return 0;
                })
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
