package com.umc.yourun.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.yourun.domain.RunningData;
import com.umc.yourun.domain.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface RunningDataRepository extends JpaRepository<RunningData, Long> {
	List<RunningData> findByStartTimeBetweenAndUser(LocalDateTime startTimeAfter, LocalDateTime startTimeBefore, User user);
    // 특정 사용자의 특정 기간 동안의 총 러닝 거리 조회 (크루원별 달린 거리 포함된 진행도 확인용)
    @Query("SELECT COALESCE(SUM(r.totalDistance), 0) " +
            "FROM RunningData r " +
            "WHERE r.user.id = :userId " +
            "AND r.startTime >= :startDate " +
            "AND r.endTime <= :endDate")
    int sumDistanceByUserIdAndPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
