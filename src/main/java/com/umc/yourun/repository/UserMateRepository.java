package com.umc.yourun.repository;

import com.umc.yourun.domain.UserMate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMateRepository extends JpaRepository<UserMate, Long> {
}
