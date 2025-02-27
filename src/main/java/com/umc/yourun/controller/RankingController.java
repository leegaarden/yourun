package com.umc.yourun.controller;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.domain.User;
import com.umc.yourun.dto.Ranking.RankingResponse;
import com.umc.yourun.repository.UserRepository;
import com.umc.yourun.service.RealtimeRankingService;
import com.umc.yourun.service.RedisRankingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Ranking", description = "Ranking API")
public class RankingController {

    private final RedisRankingService rankingService;
    private final UserRepository userRepository;


    @GetMapping("/ranking")
    public ApiResponse<RankingResponse.rankingInfoUser> getRankingWithRank(
            @RequestParam(defaultValue = "0") int page,
            @RequestHeader(value = "Authorization") String accessToken
    ) {
//
//        //이상한 페이지 요청은 모두 0으로
//        page = page < 0 ? 0 : page;

        RankingResponse.rankingInfoUser ranking = rankingService.getRankers(page, accessToken);

        return ApiResponse.success("랭킹 리스트의 반환", ranking);
    }
}
