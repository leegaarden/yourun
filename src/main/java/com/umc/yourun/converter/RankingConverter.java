package com.umc.yourun.converter;

import com.umc.yourun.domain.User;
import com.umc.yourun.domain.UserTag;
import com.umc.yourun.dto.Ranking.RankingResponse;
import com.umc.yourun.dto.Ranking.RankingResult;
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

    public static RankingResponse.rankingInfoUser toRankingInfoUser(RankingResult result) {

        User user = result.getUser();
        Long rank = (long) result.getUserRank();
        Map<User, Integer> rankings = result.getRanking();
        List<RankingResponse.rankingMateInfo> list;

//        log.info("ranking data converting" + rankings.size());
        if (rankings == null) {
            list = null;
        } else {
            list = rankings.entrySet()
                    .stream()
                    .map(RankingConverter::toRankingRealtimeInfo)
                    .collect(Collectors.toList());
        }


        return RankingResponse.rankingInfoUser.builder()
                .rank(rank)
                .username(user.getNickname())
                .tendency(user.getTendency())
                .list(list)
                .build();
    }
}
