package com.umc.yourun.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.yourun.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
