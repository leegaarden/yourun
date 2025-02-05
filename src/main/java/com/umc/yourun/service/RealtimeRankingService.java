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

import java.time.LocalDateTime;
import java.util.*;
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

        List<RunningData> runningDataList = runningDataRepository.findAllByEndTimeAfter(LocalDateTime.now().minusMonths(1));

        Map<User, Integer> rankingScore = runningDataList.stream()
                .collect(Collectors.groupingBy(
                        RunningData::getUser,
                        Collectors.summingInt(RunningData::getTotalDistance) // 총 거리 합산
                ));

        Map<User, Integer> sortedRanking = rankingScore.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) // 내림차순 정렬
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // 병합 로직 (충돌 발생 시 첫 번째 값 유지)
                        LinkedHashMap::new // 순서를 유지하는 Map 사용
                ));

        // 요청한 유저의 등수 계산
        int rank = calculateUserRank(requestedUser, sortedRanking);

        //페이지 처리
        Map<User, Integer> paginatedScores = pagenation(page, sortedRanking);

        //각 랭킹의 사람들 데이터 처리
        List<RankingResponse.rankingMateInfo> list = paginatedScores.entrySet()
                .stream()
                .map(RankingConverter::toRankingRealtimeInfo)
                .collect(Collectors.toList());

        //유저의 순위와 랭킹 정보 전달
        return RankingResponse.rankingInfoUser
                .builder()
                .rank(rank)
                .username(requestedUser.getNickname())
                .list(list)
                .build();
    }

    private static Map<User, Integer> pagenation(int page, Map<User, Integer> sortedRanking) {
        int totalSize = sortedRanking.size();
        int totalPages = (int) Math.ceil((double) totalSize / PAGE_SIZE);

        int safePage = Math.min(page, totalPages - 1);
        if (safePage < 0) safePage = 0; // 0 이하 페이지 방지


        // 페이지네이션 적용
        Map<User, Integer> paginatedScores = sortedRanking.entrySet().stream()
                .skip((long) safePage * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        return paginatedScores;
    }

    private static int calculateUserRank(User requestedUser, Map<User, Integer> sortedRanking) {
        int rank = 1;
        for (User user : sortedRanking.keySet()) {
            if (user.equals(requestedUser)) {
                break;
            }
            rank++;
        }

        //러닝 데이터에 조회 유저의 데이터가 없는 경우
        if (rank > sortedRanking.size()) {
            rank = 0;
        }
        return rank;
    }
}
