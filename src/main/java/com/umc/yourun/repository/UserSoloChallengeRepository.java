package com.umc.yourun.repository;

import com.umc.yourun.domain.enums.ChallengeStatus;
import com.umc.yourun.domain.mapping.UserSoloChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSoloChallengeRepository extends JpaRepository<UserSoloChallenge, Long> {
    boolean existsByUserIdAndSoloChallenge_ChallengeStatus(Long userId, ChallengeStatus challengeStatus);
    Optional<UserSoloChallenge> findBySoloChallengeId(Long challengeId);
}