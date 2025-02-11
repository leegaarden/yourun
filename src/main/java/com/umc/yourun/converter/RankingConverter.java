package com.umc.yourun.converter;

import com.umc.yourun.domain.User;
import com.umc.yourun.domain.UserTag;
import com.umc.yourun.dto.Ranking.RankingResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class RankingConverter {


    public static RankingResponse.rankingMateInfo toRankingRealtimeInfo(Map.Entry<User, Integer> maps) {

        List<String> collect = maps.getKey().getUserTags().stream()
                .map(userTag -> userTag.getTag().toString())
                .collect(Collectors.toList());
        ;
        return RankingResponse.rankingMateInfo
                .builder()
                .nickname(maps.getKey().getNickname())
                .score(maps.getValue())
                .tags(collect)
                .tendency(maps.getKey().getTendency())
                .build();
    }

    public static RankingResponse.rankingInfoUser toRankingInfoUser(User user, Long rank, Map<User, Integer> rankings) {

        log.info("ranking data converting" + rankings.size());
        List<RankingResponse.rankingMateInfo> list = rankings.entrySet()
                .stream()
                .map(RankingConverter::toRankingRealtimeInfo)
                .collect(Collectors.toList());

        return RankingResponse.rankingInfoUser.builder()
                .rank(rank)
                .username(user.getNickname())
                .tendency(user.getTendency())
                .list(list)
                .build();
    }
}
