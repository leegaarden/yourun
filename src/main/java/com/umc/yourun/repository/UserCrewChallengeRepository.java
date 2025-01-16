package com.umc.yourun.repository;

import com.umc.yourun.domain.enums.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umc.yourun.domain.mapping.UserCrewChallenge;

@Repository
public interface UserCrewChallengeRepository extends JpaRepository<UserCrewChallenge, Long> {
    boolean existsByUserIdAndCrewChallenge_ChallengeStatus(Long userId, ChallengeStatus challengeStatus);

}
