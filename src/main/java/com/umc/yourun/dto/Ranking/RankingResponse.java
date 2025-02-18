package com.umc.yourun.dto.Ranking;

import com.umc.yourun.domain.enums.Tendency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

public class RankingResponse {


    @Builder
    public record rankingMateInfo(

        @Schema(description = "닉네임", example = "홍길동")
        String nickname,

        @Schema(description = "score", example = "100")
        int score,

        @Schema(description = "유저 태그", example = "에너자이저")
        List<String> tags,

        @Schema(description = "tendency", example = "페이스메이커")
        Tendency tendency
    ) {}

    @Builder
    public record rankingInfoUser(

        @Schema(description = "닉네임", example = "홍길동")
        String username,

        @Schema(description = "rank", example = "10")
        Long rank,

        @Schema(description = "tendency", example = "페이스메이커")
        Tendency tendency,

        List<rankingMateInfo> list

    ) {}
}
