package com.umc.yourun.repository;

import com.umc.yourun.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.yourun.domain.RunningData;

import java.util.List;

public interface RunningDataRepository extends JpaRepository<RunningData, Long> {

    List<RunningData> findByUser(User user);
}
