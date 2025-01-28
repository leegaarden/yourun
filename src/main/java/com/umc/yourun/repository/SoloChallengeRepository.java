package com.umc.yourun.repository;

import com.umc.yourun.domain.CrewChallenge;
import com.umc.yourun.domain.SoloChallenge;
import com.umc.yourun.domain.enums.ChallengeDistance;
import com.umc.yourun.domain.enums.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SoloChallengeRepository extends JpaRepository<SoloChallenge, Long> {
    // 상태 별 조회 (대기 중, 진행 중 api 관련)
    List<SoloChallenge> findByChallengeStatus(ChallengeStatus status);

    // 기간 내 챌린지 조회 (매칭 관련)
    List<SoloChallenge> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    // PENDING 상태인 챌린지를 랜덤하게 5개 조회
    @Query(value = "SELECT * FROM solo_challenge WHERE challenge_status = 'PENDING' ORDER BY RAND() LIMIT :size",
            nativeQuery = true)
    List<SoloChallenge> findRandomPendingChallenges(@Param("size") int size);

    // 마감 시간 지난 챌린지 조회
    List<SoloChallenge> findByChallengeStatusAndEndDateBefore(
            ChallengeStatus status,
            LocalDate date
    );

}