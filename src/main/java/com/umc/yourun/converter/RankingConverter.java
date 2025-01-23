package com.umc.yourun.converter;

import com.umc.yourun.domain.Ranking;
import com.umc.yourun.domain.User;
import com.umc.yourun.dto.Ranking.RankingResponse;

import java.util.Map;

public class RankingConverter {


    public static RankingResponse.rankingMateInfo toRankingMateInto(Ranking ranking) {
        return RankingResponse.rankingMateInfo
                .builder()
                .nickname(ranking.getUser().getNickname())
                .score(ranking.getScore())
                .build();

    }

    public static RankingResponse.rankingMateInfo toRankingRealtimeInfo(Map.Entry<User, Integer> maps) {
        return RankingResponse.rankingMateInfo
                .builder()
                .nickname(maps.getKey().getNickname())
                .score(maps.getValue())
                .build();
    }
}
