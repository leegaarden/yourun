package com.umc.yourun.repository;

import com.umc.yourun.domain.SoloChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoloChallengeRepository extends JpaRepository<SoloChallenge, Long> {
}