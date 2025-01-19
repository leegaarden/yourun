package com.umc.yourun.repository;

import com.umc.yourun.domain.enums.ChallengeStatus;
import com.umc.yourun.domain.mapping.UserSoloChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSoloChallengeRepository extends JpaRepository<UserSoloChallenge, Long> {

    // 사용자의 챌린지 상태 찾기 (하나의 솔로 챌린지의 생성 및 참여를 위해)
    boolean existsByUserIdAndSoloChallenge_ChallengeStatus(Long userId, ChallengeStatus challengeStatus);

    // 솔로 챌린지 찾기
    Optional<UserSoloChallenge> findBySoloChallengeId(Long challengeId);

    // 솔로 챌린지 삭제
    void deleteAllBySoloChallengeId(Long challengeId);

}