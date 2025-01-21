package com.umc.yourun.controller;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.converter.RankingConverter;
import com.umc.yourun.domain.Ranking;
import com.umc.yourun.dto.Ranking.RankingResponse;
import com.umc.yourun.service.RankingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Ranking", description = "Ranking API")
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/distance-ranking")
    public ApiResponse<List<RankingResponse.rankingMateInfo>> getDistanceRanking() {
        List<RankingResponse.rankingMateInfo> rankingMateInfos = rankingService.getDistanceRanking()
                .stream()
                .map(RankingConverter::toRankingMateInto)
                .collect(Collectors.toList());
        return ApiResponse.success("거리에 따른 등수 리스트 반환", rankingMateInfos);
    }

    @GetMapping("/pace-ranking")
    public ApiResponse<List<RankingResponse.rankingMateInfo>> getPaceRanking() {
        List<RankingResponse.rankingMateInfo> rankingMateInfos = rankingService.getPaceRanking()
                .stream()
                .map(RankingConverter::toRankingMateInto)
                .collect(Collectors.toList());
        return ApiResponse.success("페이스에 따른 등수 리스트 반환", rankingMateInfos);
    }




}
