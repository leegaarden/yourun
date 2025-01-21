package com.umc.yourun.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.yourun.domain.RunningData;
import com.umc.yourun.domain.User;

public interface RunningDataRepository extends JpaRepository<RunningData, Long> {
	List<RunningData> findByStartTimeBetweenAndUser(LocalDateTime startTimeAfter, LocalDateTime startTimeBefore, User user);
}
