package com.umc.yourun.dto.Ranking;

import com.umc.yourun.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
public class RankingResult {
    private final User user;
    private final Map<User, Integer> ranking;
    private final int userRank;

    public RankingResult(User user, Map<User, Integer> ranking, int userRank) {
        this.user = user;
        this.ranking = ranking;
        this.userRank = userRank;
    }
}
