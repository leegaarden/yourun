package com.umc.yourun.service;

import com.umc.yourun.domain.Ranking;
import com.umc.yourun.domain.RunningData;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.RankingType;
import com.umc.yourun.repository.RankingRepository;
import com.umc.yourun.repository.RunningDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RankingService {

    private final RankingRepository rankingRepository;
    private final RunningDataRepository runningDataRepository;

    @Autowired
    public RankingService(RankingRepository rankingRepository, RunningDataRepository runningDataRepository) {
        this.rankingRepository = rankingRepository;
        this.runningDataRepository = runningDataRepository;
    }

    // 전체 PACE 랭킹 조회
    public List<Ranking> getPaceRanking() {
        return rankingRepository.findByRankingTypeAndScoreGreaterThanOrderByScoreDesc(RankingType.PACE, 0);
    }

    // 전체 DISTANCE 랭킹 조회
    public List<Ranking> getDistanceRanking() {
        return rankingRepository.findByRankingTypeAndScoreGreaterThanOrderByScoreDesc(RankingType.DISTANCE, 0);
    }

    // 개인 PACE 랭킹 조회
    public Optional<Ranking> getPersonalPaceRanking(User user) {
        return rankingRepository.findByUserAndRankingType(user, RankingType.PACE);
    }

    // 개인 DISTANCE 랭킹 조회
    public Optional<Ranking> getPersonalDailyRanking(User user) {
        return rankingRepository.findByUserAndRankingType(user, RankingType.DISTANCE);
    }

    // 신규 랭킹 추가
    public void addRanking(User user, int score, RankingType type) {
        Ranking ranking = Ranking.builder()
                .user(user)
                .score(score)
                .rankingType(type)
                .build();
        rankingRepository.save(ranking);
    }

    //하루에 한번 업데이트를 해야한다.
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateDistanceRanking() {

        List<Ranking> rankings = rankingRepository.findAllByRankingType(RankingType.DISTANCE);

        for (Ranking ranking : rankings) {
            List<RunningData> runningDatas = runningDataRepository.findByUser(ranking.getUser());

            int newScore = runningDatas.stream()
                    .mapToInt(RunningData::getTotalDistance)
                    .sum();

            ranking.updateScore(newScore);
        }

        rankingRepository.saveAll(rankings);
    }
}
