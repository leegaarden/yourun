package com.umc.yourun.repository;

import com.umc.yourun.domain.CrewChallenge;
import com.umc.yourun.domain.enums.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CrewChallengeRepository extends JpaRepository<CrewChallenge, Long> {

    // 상태 별 조회 (대기 중, 진행 중 api 관련)
    List<CrewChallenge> findByChallengeStatus(ChallengeStatus status);

    // 기간 내 챌린지 조회 (매칭 관련)
    List<CrewChallenge> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

}
