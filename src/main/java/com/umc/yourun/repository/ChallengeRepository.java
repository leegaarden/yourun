package com.umc.yourun.repository;

import com.umc.yourun.domain.challenge.Challenge;
import com.umc.yourun.domain.enums.ChallengeDistance;
import com.umc.yourun.domain.enums.ChallengeKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    // 특정 기간 내에 있는 챌린지 찾기
    List<Challenge> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate endDate, LocalDate startDate);

    // 특정 기간 내의 특정 종류 챌린지 찾기
    List<Challenge> findByKindAndStartDateBetween(
            ChallengeKind kind, LocalDate startDate, LocalDate endDate);

    // 종류별(CREW/SOLO) 챌린지 찾기
    List<Challenge> findByKind(ChallengeKind kind);

    // 거리별(1km, 3km, 5km) 챌린지 찾기
    List<Challenge> findByDistance(ChallengeDistance distance);

    // 특정 크루의 진행중인 챌린지 찾기
    Optional<Challenge> findByCrewIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long crewId, LocalDate currentDate, LocalDate currentDate2);

}