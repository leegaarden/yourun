package com.umc.yourun.service;

import com.umc.yourun.config.JwtTokenProvider;
import com.umc.yourun.converter.RankingConverter;
import com.umc.yourun.domain.RunningData;
import com.umc.yourun.domain.User;
import com.umc.yourun.dto.Ranking.RankingResponse;
import com.umc.yourun.dto.Ranking.RankingResult;
import com.umc.yourun.repository.RunningDataRepository;
import com.umc.yourun.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
        log.info("save user running record" + userId);
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();

        LocalDateTime aMonthAgo = LocalDateTime.now().minusMonths(1);
        List<RunningData> records = runningDataRepository.findByUserIdAndStartTimeAfter(userId, aMonthAgo);

        int totalDistance = records.stream().mapToInt(RunningData::getTotalDistance).sum();

        if (totalDistance > 0) {
            log.info("User {} 랭킹 데이터 Redis 저장. 총 거리: {}", userId, totalDistance);
            zSetOperations.add(RUNNING_RANK_KEY, String.valueOf(userId), totalDistance);        //덮어쓰기
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


        RankingResult ranking = pagenation(page, user.getId());
        RankingResponse.rankingInfoUser rankingInfoUser = RankingConverter.toRankingInfoUser(ranking);

//        log.info("[Ranking Request] User: {}, ID: {}, Requested Page: {}, Rank: {}",
//                user.getNickname(), user.getId(), page, userRank);

        return rankingInfoUser;
    }

    private RankingResult pagenation(int page, Long userId) {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        User requestUser = userRepository.findById(userId).get();

        //Redis에 저장된 자신의 친구 리스트 불러오기
        Set<String> friends = redisTemplate.opsForSet().members("friends:" + userId);
        if (friends == null || friends.isEmpty()) {
            log.info("유저 {}의 친구가 없음", userId);
            return new RankingResult(requestUser, null, -1);
        }

        //랭킹에서 친구들만 필터링
        List<Long> friendIds = friends.stream().map(Long::parseLong).collect(Collectors.toList());
        friendIds.add(userId);
        Set<ZSetOperations.TypedTuple<String>> rankedUsers = zSetOperations.reverseRangeWithScores(RUNNING_RANK_KEY, 0, -1);

        if (rankedUsers == null || rankedUsers.isEmpty()) {
            log.info("랭킹 데이터 없음");
            return new RankingResult(requestUser, null, -1);
        }

        //친구 ID에 해당하는 데이터만 필터링
        List<Long> userRanker = rankedUsers.stream()
                .filter(ob -> friendIds.contains(Long.parseLong(ob.getValue())))
                .map(ob -> Long.parseLong(ob.getValue()))
                .collect(Collectors.toList());

        //유저 데이터 조회
        Map<Long, User> userMap = userRepository.findAllById(userRanker).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        Map<User, Integer> result = new LinkedHashMap<>();
        for (ZSetOperations.TypedTuple<String> tuple : rankedUsers) {
            Long rankedUserId = Long.parseLong(tuple.getValue());
            User rankingUser = userMap.get(rankedUserId);

            result.put(rankingUser, tuple.getScore().intValue());
        }

        int userRankInResult = getUserRankInResult(result, requestUser);
        RankingResult rankingResult = new RankingResult(requestUser, result, userRankInResult);

        return rankingResult;
    }

    private int getUserRankInResult(Map<User, Integer> result, User targetUser) {
        int rank = 1;
        for (User user : result.keySet()) {
            if (user.equals(targetUser)) {
                return rank;
            }
            rank++;
        }
        return -1; // 랭킹에 없을 경우
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
        log.info("1개월 이상 지난 데이터를 Redis에서 정리 완료");
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
