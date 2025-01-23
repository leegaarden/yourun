package com.umc.yourun.controller;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.converter.RankingConverter;
import com.umc.yourun.domain.Ranking;
import com.umc.yourun.domain.User;
import com.umc.yourun.dto.Ranking.RankingResponse;
import com.umc.yourun.repository.UserRepository;
import com.umc.yourun.service.RankingService;
import com.umc.yourun.service.RealtimeRankingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Ranking", description = "Ranking API")
public class RankingController {

    private final RealtimeRankingService rankingService;
    private final UserRepository userRepository;

//    @GetMapping("/distance-ranking")
//    public ApiResponse<List<RankingResponse.rankingMateInfo>> getDistanceRanking() {
//        List<RankingResponse.rankingMateInfo> rankingMateInfos = rankingService.getDistanceRanking()
//                .stream()
//                .map(RankingConverter::toRankingMateInto)
//                .collect(Collectors.toList());
//        return ApiResponse.success("거리에 따른 등수 리스트 반환", rankingMateInfos);
//    }
//
//    @GetMapping("/pace-ranking")
//    public ApiResponse<List<RankingResponse.rankingMateInfo>> getPaceRanking() {
//        List<RankingResponse.rankingMateInfo> rankingMateInfos = rankingService.getPaceRanking()
//                .stream()
//                .map(RankingConverter::toRankingMateInto)
//                .collect(Collectors.toList());
//        return ApiResponse.success("페이스에 따른 등수 리스트 반환", rankingMateInfos);
//    }

//    @GetMapping("/ranking")
//    public ApiResponse<List<RankingResponse.rankingMateInfo>> getRanking(
//            @RequestParam(defaultValue = "0") int page
//    ) {
//
//        //이상한 페이지 요청은 모두 0으로
//        page = page < 0 ? 0 : page;
//
//        List<RankingResponse.rankingMateInfo> rankingList = rankingService.getRanking(page).entrySet()
//                .stream()
//                .map(RankingConverter::toRankingRealtimeInfo)
//                .collect(Collectors.toList());
//        return ApiResponse.success("랭킹 리스트의 반환", rankingList);
//    }

    @GetMapping("/ranking/rank")
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
