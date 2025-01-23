package com.umc.yourun.controller;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.domain.User;
import com.umc.yourun.dto.Ranking.RankingResponse;
import com.umc.yourun.repository.UserRepository;
import com.umc.yourun.service.RealtimeRankingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Ranking", description = "Ranking API")
public class RankingController {

    private final RealtimeRankingService rankingService;
    private final UserRepository userRepository;


    @GetMapping("/ranking")
    public ApiResponse<RankingResponse.rankingInfoUser> getRankingWithRank(
            @RequestParam(defaultValue = "0") int page
    ) {

        //이상한 페이지 요청은 모두 0으로
        page = page < 0 ? 0 : page;

        //TODO: 유저 관련 수정
        User user = userRepository.getById(1L);

        RankingResponse.rankingInfoUser ranking = rankingService.getRanking(page, user);

        return ApiResponse.success("랭킹 리스트의 반환", ranking);
    }
}
