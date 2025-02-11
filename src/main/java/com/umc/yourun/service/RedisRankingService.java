package com.umc.yourun.service;

import com.umc.yourun.config.JwtTokenProvider;
import com.umc.yourun.converter.RankingConverter;
import com.umc.yourun.domain.RunningData;
import com.umc.yourun.domain.User;
import com.umc.yourun.dto.Ranking.RankingResponse;
import com.umc.yourun.repository.RunningDataRepository;
import com.umc.yourun.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@Slf4j
@RequiredArgsConstructor
public class RedisRankingService {

    private static final int PAGE_SIZE = 10;  // 페이지 크기 상수 설정

    private final StringRedisTemplate redisTemplate;
    private static final String RUNNING_RANK_KEY = "running_rank";
    private final RunningDataRepository runningDataRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;


    /**
     * 한달간의 러닝 기록을 불러와 기록을 Redis에 저장한다.
     * 만약 유저의 최근 한달 데이터가 없다면 Redis의 랭킹에서 삭제한다.
     * @param userId
     */
    @Transactional
    public void saveUserRunningRecordInRedis(Long userId) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();

        LocalDateTime aMonthAgo = LocalDateTime.now().minusMonths(1);
        List<RunningData> records = runningDataRepository.findByUserIdAndStartTimeAfter(userId, aMonthAgo);

        int totalDistance = records.stream().mapToInt(RunningData::getTotalDistance).sum();

        if (totalDistance > 0) {
            log.info("User {} 랭킹 데이터 Redis 저장. 총 거리: {}", userId, totalDistance);
            zSetOperations.add(RUNNING_RANK_KEY, String.valueOf(userId), totalDistance);
        } else {
            zSetOperations.remove(RUNNING_RANK_KEY, String.valueOf(userId));
        }
    }

    /**
     * 유저의 랭킹 조회
     *  유저가 랭킹에 없는 경우 -> 0
     *  랭킹은 1부터 시작하도록
     * @param userId
     * @return
     */
    public Long getUserRank(Long userId) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        Long rank = zSetOperations.reverseRank(RUNNING_RANK_KEY, userId.toString());

        if (rank == null) {
            rank = 0L;
        } else {
            rank++;
        }

        return rank;
    }

    /**
     * 유저 요청에 따른 등수와 랭킹 반환
     * @param page, accessToken
     * @return
     */
    public RankingResponse.rankingInfoUser getRankers(int page, String accessToken) {

        User user = jwtTokenProvider.getUserByToken(accessToken);

        Long userRank = getUserRank(user.getId());

        Map<User, Integer> ranking = pagenation(page);
        RankingResponse.rankingInfoUser rankingInfoUser = RankingConverter.toRankingInfoUser(user, userRank, ranking);

        log.info("[Ranking Request] User: {}, ID: {}, Requested Page: {}, Rank: {}",
                user.getNickname(), user.getId(), page, userRank);

        return rankingInfoUser;
    }

    private Map<User, Integer> pagenation(int page) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();

        Long size = zSetOperations.size(RUNNING_RANK_KEY);
        log.info("Redis size: " + zSetOperations.size(RUNNING_RANK_KEY));

        int totalPages = (int) Math.ceil((double) size / PAGE_SIZE);
        int safePage = Math.min(Math.max(page, 0), totalPages - 1);
        int start = safePage * PAGE_SIZE;
        int end = start + PAGE_SIZE - 1;

        // 페이지네이션 적용
        Set<ZSetOperations.TypedTuple<String>> rankedUsers = zSetOperations.reverseRangeWithScores(RUNNING_RANK_KEY, start, end);
        if (rankedUsers == null || rankedUsers.isEmpty()) {
            log.info("ranking is empty");
            return Collections.emptyMap();
        }

        List<Long> userRanker = rankedUsers.stream().map(list -> Long.parseLong(list.getValue()))
                .collect(Collectors.toList());

        Map<Long, User> userMap = userRepository.findAllById(userRanker).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        Map<User, Integer> result = new LinkedHashMap<>();
        for (ZSetOperations.TypedTuple<String> tuple : rankedUsers) {
            Long userId = Long.parseLong(tuple.getValue());
            User rankingUser = userMap.get(userId);
            if (rankingUser != null) {
                result.put(rankingUser, tuple.getScore().intValue());
            }
        }
        log.info("int" + result.size());

        return result;
    }

    /**
     * 이 매서드를 주기적으로 사용하여 새롭게 계산: 한달이 지난 데이터는 제거하고 랭킹을 새롭게 만든다.
     */
    @Transactional
    public void removeOldRecords() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        Set<String> allUsers = zSetOperations.range(RUNNING_RANK_KEY, 0, -1);


        //기존에 Redis에 존재하던 유저들을 대상으로 데이터를 업데이트한다.
        for (String userId : allUsers) {
            saveUserRunningRecordInRedis(Long.parseLong(userId));
        }
        log.info("1개월 이상 지난 데이터를 Redis에서 정리 완료" + zSetOperations.size(RUNNING_RANK_KEY));
    }

    @Transactional
    public void resetRedisRecords() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        LocalDateTime aMonthAgo = LocalDateTime.now().minusMonths(1);

        Set<String> allUsers = Optional.ofNullable(runningDataRepository.findAllByStartTimeAfter(aMonthAgo))
                .orElse(Collections.emptyList())
                .stream()
                .map(RunningData::getUser)
                .filter(Objects::nonNull)
                .map(user -> String.valueOf(user.getId()))
                .collect(Collectors.toSet());

        zSetOperations.removeRange(RUNNING_RANK_KEY, 0 ,-1);

        for (String userId : allUsers) {
            saveUserRunningRecordInRedis(Long.parseLong(userId));
        }
    }
}
