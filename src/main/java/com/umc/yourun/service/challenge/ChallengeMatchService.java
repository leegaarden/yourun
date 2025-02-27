package com.umc.yourun.service.challenge;

import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.custom.ChallengeException;
import com.umc.yourun.domain.CrewChallenge;
import com.umc.yourun.domain.SoloChallenge;
import com.umc.yourun.domain.enums.ChallengeStatus;
import com.umc.yourun.repository.CrewChallengeRepository;
import com.umc.yourun.repository.SoloChallengeRepository;
import com.umc.yourun.repository.UserCrewChallengeRepository;
import com.umc.yourun.repository.UserSoloChallengeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// 챌린지 매칭과 관련된 서비스 클래스
@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeMatchService {


    private final SoloChallengeRepository soloChallengeRepository;
    private final CrewChallengeRepository crewChallengeRepository;
    private final UserSoloChallengeRepository userSoloChallengeRepository;
    private final UserCrewChallengeRepository userCrewChallengeRepository;

    // 1분 마다 매칭되지 않은 솔로 첼린지 체크
    @Scheduled(fixedRate = 60000)
    public void checkUnmatchedSoloChallenges() {
        List<SoloChallenge> pendingSoloChallenges = soloChallengeRepository
                .findByChallengeStatus(ChallengeStatus.PENDING);

        for (SoloChallenge challenge : pendingSoloChallenges) {
            if (!challenge.isMatchable()) {
                deleteSoloChallenge(challenge);
            }
        }
    }

    // 1분마다 4명이 결성도 되지 않은 크루 챌린지 체크
    @Scheduled(fixedRate = 60000)  // 60000ms = 1분
    public void checkUnJoin4CrewChallenges() {
        // PENDING 상태의 크루 챌린지 조회
        List<CrewChallenge> pendingCrewChallenges = crewChallengeRepository
                .findByChallengeStatus(ChallengeStatus.PENDING);

        for (CrewChallenge challenge : pendingCrewChallenges) {
            // 현재 참여 인원 확인
            long memberCount = userCrewChallengeRepository.countByCrewChallengeId(challenge.getId());

            // 4명 미만이고 24시간 초과된 경우 삭제
            if (memberCount < 4 && !challenge.isMatchable()) {
                deleteCrewChallenge(challenge);
            }
        }
    }

    // 1분마다 매칭 시도
    @Scheduled(fixedRate = 60000)  // 60000ms = 1분
    public void attemptCrewMatching() {
        // 4명이 모인 PENDING 상태의 크루 챌린지들 조회 (조건은 아직 적용 전)
        List<CrewChallenge> readyCrewChallenges = crewChallengeRepository
                .findPendingCrewsWithFourMembers();

        for (CrewChallenge challenge : readyCrewChallenges) {
            // 24시간 초과 체크
            if (!challenge.isMatchable()) {
                deleteCrewChallenge(challenge);
                continue;
            }

            // 매칭 시도 (이 메소드에서 조건 적용)
            tryMatchCrewChallenge(challenge);
        }
    }

    // 기간 설정 등의 조건 적용하여 매칭 시도
    private void tryMatchCrewChallenge(CrewChallenge challenge) {
        // 같은 기간의 다른 4명이 모인 크루 찾기
        List<CrewChallenge> potentialMatches = crewChallengeRepository
                .findFirstMatchableCrewOrderByCreatedAt(
                        challenge.getChallengePeriod(),
                        challenge.getId()
                );

        // 매칭 가능한 크루가 있으면 가장 먼저 생성된 크루와 매칭 (순서대로)
        if (!potentialMatches.isEmpty()) {
            CrewChallenge matchedChallenge = potentialMatches.get(0);

            // 두 크루 모두 상태 업데이트
            challenge.updateStatus(ChallengeStatus.IN_PROGRESS);
            challenge.setMatchedCrewChallengeId(matchedChallenge.getId());  // 매칭된 크루 정보 저장

            matchedChallenge.updateStatus(ChallengeStatus.IN_PROGRESS);
            matchedChallenge.setMatchedCrewChallengeId(challenge.getId());  // 매칭된 크루 정보 저장
        }

    }

    // 1분 마다 마감시간이 된 챌린지들 상태 COMPLETED 로 변경
    @Scheduled(fixedRate = 60000) // 1분 마다 실행
    public void completeExpiredChallenges() {
        LocalDateTime now = LocalDateTime.now();

        // 1. 솔로 챌린지 종료 처리
        List<SoloChallenge> expiredSoloChallenges = soloChallengeRepository
                .findByChallengeStatusAndEndDateBefore(
                        ChallengeStatus.IN_PROGRESS,
                        now
                );

        for (SoloChallenge challenge : expiredSoloChallenges) {
            challenge.updateStatus(ChallengeStatus.COMPLETED);
            soloChallengeRepository.save(challenge);
        }

        // 2. 크루 챌린지 종료 처리 (수정된 로직 : 두 크루 챌린지 모두 마감시간이 되어야 COMPLETED 상태가 되도록)
        List<CrewChallenge> expiredCrewChallenges = crewChallengeRepository
                .findByChallengeStatusAndEndDateBefore(
                        ChallengeStatus.IN_PROGRESS,
                        now
                );

        for (CrewChallenge challenge : expiredCrewChallenges) {
            // 매칭된 크루 챌린지 조회
            CrewChallenge matchedChallenge = crewChallengeRepository
                    .findById(challenge.getMatchedCrewChallengeId())
                    .orElseThrow(() -> new ChallengeException(ErrorCode.CHALLENGE_NOT_FOUND));

            // 둘 중 더 늦은 종료 시간 확인
            LocalDateTime laterEndDate = challenge.getEndDate().isAfter(matchedChallenge.getEndDate())
                    ? challenge.getEndDate()
                    : matchedChallenge.getEndDate();

            // 현재 시간이 더 늦은 종료 시간을 지났을 경우에만 두 챌린지 모두 COMPLETED로 변경
            if (now.isAfter(laterEndDate)) {
                challenge.updateStatus(ChallengeStatus.COMPLETED);
                matchedChallenge.updateStatus(ChallengeStatus.COMPLETED);
                crewChallengeRepository.save(challenge);
                crewChallengeRepository.save(matchedChallenge);
            }
        }
    }

    // 유저솔로챌린지, 솔로챌린지 삭제
    private void deleteSoloChallenge(SoloChallenge challenge) {
        userSoloChallengeRepository.deleteAllBySoloChallengeId(challenge.getId());
        soloChallengeRepository.delete(challenge);
    }

    // 유저크루챌린지, 크루챌린지 삭제
    private void deleteCrewChallenge(CrewChallenge challenge) {
        userCrewChallengeRepository.deleteAllByCrewChallengeId(challenge.getId());
        crewChallengeRepository.delete(challenge);
    }

}
