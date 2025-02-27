package com.umc.yourun.scheduler;

import com.umc.yourun.service.RedisRankingService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingUpdateScheduler {

    private final RedisRankingService redisRankingService;

    /**
     * 서버가 재시작될 때 실행
     */
    @PostConstruct
    public void onStartup() {
        log.info("🔥 서버 시작: Redis 랭킹 데이터 정리 실행");
        redisRankingService.resetRedisRecords();
        log.info("🔥 서버 시작: Redis Mate 데이터 정리 실행");
        redisRankingService.resetRedisFriendsRecords();
    }

    /**
     * 매일 자정(00:00)에 실행
     */
    @Scheduled(cron = "0 0 0 * * ?") // 매일 00:00:00 실행
    public void cleanupOldRecords() {
        log.info("자정 스케줄 실행: Redis 랭킹 데이터 정리 시작");
        redisRankingService.removeOldRecords();
        log.info("자정 스케줄 실행: Redis Mate 데이터 정리 실행");
        redisRankingService.resetRedisFriendsRecords();
        log.info("자정 스케줄 완료: Redis 랭킹 데이터 정리 완료");
    }
}
