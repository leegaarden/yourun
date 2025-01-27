package com.umc.yourun.service;

import com.umc.yourun.config.JwtTokenProvider;
import com.umc.yourun.converter.RankingConverter;
import com.umc.yourun.domain.RunningData;
import com.umc.yourun.domain.User;
import com.umc.yourun.dto.Ranking.RankingResponse;
import com.umc.yourun.repository.RunningDataRepository;
import com.umc.yourun.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RealtimeRankingService {

    private static final int PAGE_SIZE = 10;  // 페이지 크기 상수 설정

    private final RunningDataRepository runningDataRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public RealtimeRankingService(RunningDataRepository runningDataRepository, UserRepository userRepository, JwtTokenProvider provider) {
        this.runningDataRepository = runningDataRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = provider;
    }

    public RankingResponse.rankingInfoUser getRanking(int page, String accessToken) {

        User requestedUser = jwtTokenProvider.getUserByToken(accessToken);

        List<User> users = userRepository.findAll();

        // 각 멤버의 총점 계산
        Map<User, Integer> scores = calculateUserScore(users);

        // 점수를 기준으로 내림차순 정렬 후 리스트로 변환
        List<Map.Entry<User, Integer>> sortedRanking = scores.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toList());

        // 요청한 유저의 등수 계산
        int rank = calculateUserRank(requestedUser, sortedRanking);


        // 페이지네이션 적용
        Map<User, Integer> paginatedScores = sortedRanking.stream()
                .skip((long) page * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        List<RankingResponse.rankingMateInfo> list = paginatedScores.entrySet()
                .stream()
                .map(RankingConverter::toRankingRealtimeInfo)
                .collect(Collectors.toList());

        return RankingResponse.rankingInfoUser
                .builder()
                .rank(rank)
                .username(requestedUser.getNickname())
                .list(list)
                .build();
    }

    private static int calculateUserRank(User requestedUser, List<Map.Entry<User, Integer>> sortedRanking) {
        int rank = 1;
        for (Map.Entry<User, Integer> entry : sortedRanking) {
            if (entry.getKey().equals(requestedUser)) {
                break;
            }
            rank++;
        }
        return rank;
    }

    private Map<User, Integer> calculateUserScore(List<User> users) {
        Map<User, Integer> scores = users.stream()
                .collect(Collectors.toMap(
                        user -> user,
                        user -> runningDataRepository.findByUser(user).stream()
                                .mapToInt(RunningData::getTotalDistance)
                                .sum()
                ));
        return scores;
    }

}
