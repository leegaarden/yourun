package com.umc.yourun.repository;

import com.umc.yourun.domain.Ranking;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.RankingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {

    List<Ranking> findByRankingTypeAndScoreGreaterThanOrderByScoreDesc(RankingType rankingType, int score);

    //내 등수 확인
    Optional<Ranking> findByUserAndRankingType(User user, RankingType rankingType);

    List<Ranking> findAll();

    List<Ranking> findAllByRankingType(RankingType rankingType);
}
