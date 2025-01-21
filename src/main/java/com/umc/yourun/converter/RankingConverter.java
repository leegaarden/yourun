package com.umc.yourun.converter;

import com.umc.yourun.domain.Ranking;
import com.umc.yourun.dto.Ranking.RankingResponse;

public class RankingConverter {


    public static RankingResponse.rankingMateInfo toRankingMateInto(Ranking ranking) {
        return RankingResponse.rankingMateInfo
                .builder()
                .nickname(ranking.getUser().getNickname())
                .score(ranking.getScore())
                .build();

    }
}
