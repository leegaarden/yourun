package com.umc.yourun.repository;

import com.umc.yourun.domain.enums.ChallengeResult;
import com.umc.yourun.domain.enums.ChallengeStatus;
import com.umc.yourun.domain.mapping.UserSoloChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSoloChallengeRepository extends JpaRepository<UserSoloChallenge, Long> {

    // 유저 아이디와 상태로 찾기
    // Optional 반환
    Optional<UserSoloChallenge> findByUserIdAndSoloChallenge_ChallengeStatusIn(
            Long userId,
            List<ChallengeStatus> statuses
    );

    // boolean 반환
    boolean existsByUserIdAndSoloChallenge_ChallengeStatusIn(
            Long userId,
            List<ChallengeStatus> statuses
    );

    // 솔로 챌린지 찾기
    Optional<UserSoloChallenge> findBySoloChallengeId(Long challengeId);

    // 솔로 챌린지 삭제
    void deleteAllBySoloChallengeId(Long challengeId);

    // 사용자의 챌린지 메이트 찾기
    Optional<UserSoloChallenge> findBySoloChallengeIdAndUserIdNot(Long challengeId, Long userId);

    // 챌린지 아이디와 생성자 여부로 찾기 (챌린지 메이트 찾기)
    Optional<UserSoloChallenge> findBySoloChallengeIdAndIsCreator(Long challengeId, boolean isCreator);

    // 현재 진행중이고, 시작된 챌린지만 조회
    List<UserSoloChallenge> findAllByChallengeResultAndSoloChallenge_StartDateLessThanEqual(
            ChallengeResult challengeResult,
            LocalDateTime dateTime
    );

    // 유저가 현재 참여 중인 솔로 챌린지 조회
    Optional<UserSoloChallenge> findFirstByUserIdOrderByCreatedAtDesc(Long userId);

}