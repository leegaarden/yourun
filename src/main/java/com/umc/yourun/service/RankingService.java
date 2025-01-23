package com.umc.yourun.service;

import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.custom.RankingException;
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
import java.util.stream.Stream;

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
    public int getPersonalPaceRanking(User user) {
        Integer integer = rankingRepository.findByUserAndRankingType(user, RankingType.PACE)
                .map(Ranking::getSortOrder)
                .orElseThrow(() -> new RankingException(ErrorCode.RANKING_NOT_FOUND));
        return (int) integer;
    }

    // 개인 DISTANCE 랭킹 조회
    public int getPersonalDistanceRanking(User user) {

        Integer integer = rankingRepository.findByUserAndRankingType(user, RankingType.DISTANCE)
                .map(Ranking::getSortOrder)
                .orElseThrow(() -> new RankingException(ErrorCode.RANKING_NOT_FOUND));
        return (int) integer;
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

    @Scheduled(cron = "0 0 0 * * ?")
    public void calculateScoreAndSort() {
        updateDistanceRankingScore();
        updateDistanceRankingSort();
    }

    //저장된 스코어를 다시 계산한다.
    public void updateDistanceRankingScore() {

        List<Ranking> rankings = rankingRepository.findAllByRankingType(RankingType.DISTANCE);

        for (Ranking ranking : rankings) {
            List<RunningData> runningDatas = runningDataRepository.findByUser(ranking.getUser());

            int newScore = runningDatas.stream()
                    .mapToInt(RunningData::getTotalDistance)
                    .sum();

            // 점수가 변경될 경우에만 업데이트 수행
            if (ranking.getScore() != newScore) {
                ranking.updateScore(newScore);
            }
        }

        rankingRepository.saveAll(rankings);
    }

    //랭킹 순위를 계산하여 저장합니다.
    public void updateDistanceRankingSort() {

        List<Ranking> rankings = rankingRepository.findAllByRankingTypeOrderByScore(RankingType.DISTANCE);

        int rank = 1; // 초기 순위
        for (int i = 0; i < rankings.size(); i++) {
            Ranking ranking = rankings.get(i);

            // 이전 순위와 점수가 다를 경우 랭크 업데이트
            if (i > 0 && rankings.get(i).getScore() < rankings.get(i - 1).getScore()) {
                rank = i + 1;
            }

            // sortOrder 업데이트
            ranking.updateSortOrder(rank);
            rankingRepository.save(ranking);
        }
    }
}
