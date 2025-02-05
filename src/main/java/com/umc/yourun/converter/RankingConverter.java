package com.umc.yourun.converter;

import com.umc.yourun.domain.User;
import com.umc.yourun.domain.UserTag;
import com.umc.yourun.dto.Ranking.RankingResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
}
