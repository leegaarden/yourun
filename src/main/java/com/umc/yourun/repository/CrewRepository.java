package com.umc.yourun.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.yourun.domain.Crew;

public interface CrewRepository extends JpaRepository<Crew, Long> {
}
