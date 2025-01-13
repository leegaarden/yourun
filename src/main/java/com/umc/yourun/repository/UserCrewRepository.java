package com.umc.yourun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umc.yourun.domain.mapping.UserCrew;

@Repository
public interface UserCrewRepository extends JpaRepository<UserCrew, Long> {

}
