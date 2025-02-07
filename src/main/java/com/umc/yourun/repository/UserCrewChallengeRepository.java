package com.umc.yourun.repository;

import com.umc.yourun.domain.enums.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umc.yourun.domain.mapping.UserCrewChallenge;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCrewChallengeRepository extends JpaRepository<UserCrewChallenge, Long> {
    // 크루 챌린지 멤버 조회 (생성 시간순)
    List<UserCrewChallenge> findByCrewChallengeIdOrderByCreatedAt(Long challengeId);

    // 상태별 사용자의 크루 챌린지 참여 여부 확인
    boolean existsByUserIdAndCrewChallenge_ChallengeStatusIn(
            Long userId,
            List<ChallengeStatus> statuses
    );

    // 상태별 사용자의 크루 챌린지 조회
    UserCrewChallenge findByUserIdAndCrewChallenge_ChallengeStatusIn(
            Long userId,
            List<ChallengeStatus> statuses
    );

    // 크루 챌린지 아이디로 생성자 찾기
    Optional<UserCrewChallenge> findByCrewChallengeIdAndIsCreator(Long crewChallengeId, boolean isCreator);

    // 챌린지 참여 인원 수 조회
    long countByCrewChallengeId(Long challengeId);

    // 크루 챌린지 삭제
    void deleteAllByCrewChallengeId(Long challengeId);

    // 사용자의 크루 챌린지 참여 정보 조회
    UserCrewChallenge findByUserId(Long userId);

    // 사용자가 현재 참여 중인 크루 챌린지 조회
    Optional<UserCrewChallenge> findFirstByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<UserCrewChallenge> findFirstByUserIdAndCrewChallenge_ChallengeStatusInOrderByCreatedAtDesc(
            Long userId,
            List<ChallengeStatus> challengeStatuses
    );

}