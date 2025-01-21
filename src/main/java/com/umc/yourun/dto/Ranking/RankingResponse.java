package com.umc.yourun.dto.Ranking;

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
        List<String> tags
    ) {}
}
