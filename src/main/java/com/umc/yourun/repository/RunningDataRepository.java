package com.umc.yourun.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.yourun.domain.RunningData;

public interface RunningDataRepository extends JpaRepository<RunningData, Long> {
}
