package com.umc.yourun.service;

import com.umc.yourun.domain.RunningData;
import com.umc.yourun.domain.User;
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
@Transactional
public class RealtimeRankingService {

    private final RunningDataRepository runningDataRepository;
    private final UserRepository userRepository;

    @Autowired
    public RealtimeRankingService(RunningDataRepository runningDataRepository, UserRepository userRepository) {
        this.runningDataRepository = runningDataRepository;
        this.userRepository = userRepository;
    }

    public Map<User, Integer> getRanking() {
        List<User> users = userRepository.findAll();

        // 각 멤버의 총점 계산
        Map<User, Integer> scores = users.stream()
                .collect(Collectors.toMap(
                        user -> user,
                        user -> runningDataRepository.findByUser(user).stream()
                                .mapToInt(RunningData::getTotalDistance)
                                .sum()
                ));

        // 점수를 기준으로 내림차순 정렬
        return scores.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new  // 순서 유지
                ));
    }

}
