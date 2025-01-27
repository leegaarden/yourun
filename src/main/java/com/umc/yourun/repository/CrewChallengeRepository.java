package com.umc.yourun.repository;

import com.umc.yourun.domain.CrewChallenge;
import com.umc.yourun.domain.enums.ChallengePeriod;
import com.umc.yourun.domain.enums.ChallengeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CrewChallengeRepository extends JpaRepository<CrewChallenge, Long> {

    // 상태 별 조회 (대기 중, 진행 중 api 관련)
    List<CrewChallenge> findByChallengeStatus(ChallengeStatus status);

    // 기간 내 챌린지 조회 (매칭 관련)
    List<CrewChallenge> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    // 같은 기간의 PENDING 상태의 크루 조회
    List<CrewChallenge> findByChallengePeriodAndChallengeStatusAndIdNot(
            ChallengePeriod period,
            ChallengeStatus status,
            Long excludeId
    );

    // dateTime 보다 이전인 특정 상태인 챌린지 조회
    List<CrewChallenge> findByCreatedAtBeforeAndChallengeStatus(
            LocalDateTime dateTime,
            ChallengeStatus challengeStatus
    );

    // 4명이 모인 PENDING 상태의 크루 챌린지 조회
    @Query("SELECT cc FROM CrewChallenge cc " +
            "WHERE cc.challengeStatus = 'PENDING' " +
            "AND (SELECT COUNT(ucc) FROM UserCrewChallenge ucc WHERE ucc.crewChallenge = cc) = 4")
    List<CrewChallenge> findPendingCrewsWithFourMembers();

    // 매칭 가능한 크루 찾기 (같은 기간, 4명, PENDING 상태)
    @Query("SELECT cc FROM CrewChallenge cc " +
            "WHERE cc.challengeStatus = 'PENDING' " +
            "AND cc.challengePeriod = :period " +
            "AND cc.id != :excludeId " +
            "AND (SELECT COUNT(ucc) FROM UserCrewChallenge ucc WHERE ucc.crewChallenge = cc) = 4 " +
            "ORDER BY cc.createdAt ASC")
    List<CrewChallenge> findFirstMatchableCrewOrderByCreatedAt(ChallengePeriod period, Long excludeId);

    // 이미 존재하는 크루명인지 확인
    boolean existsByCrewNameIgnoreCase(String name);

    // PENDING 상태인 챌린지를 랜덤하게 5개 조회
    @Query(value = "SELECT * FROM crew_challenge WHERE challenge_status = 'PENDING' ORDER BY RAND() LIMIT :size",
            nativeQuery = true)
    List<CrewChallenge> findRandomPendingChallenges(@Param("size") int size);

}
