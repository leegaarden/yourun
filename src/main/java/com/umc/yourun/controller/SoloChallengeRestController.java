package com.umc.yourun.controller;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.config.exception.ErrorResponse;
import com.umc.yourun.dto.challenge.ChallengeRequest;
import com.umc.yourun.dto.challenge.ChallengeResponse;
import com.umc.yourun.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/challenges/solo")
@Tag(name = "SoloChallenge", description = "솔로 챌린지 API")
public class SoloChallengeRestController {

    private final ChallengeService challengeService;

    @Operation(summary = "SOLO_CHALLENGE_API_01 : 솔로 챌린지 생성", description = "새로운 솔로 챌린지를 생성합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "챌린지 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping()
    public ApiResponse<Long> createSoloChallenge(
            // @RequestHeader("Authorization") String token,
            @RequestHeader("USER-ID") Long userId, // TODO: 토큰 구현시 수정
            @RequestBody @Valid ChallengeRequest.CreateSoloChallengeReq request) {
        Long challengeId = challengeService.createSoloChallenge(request, userId);
        return ApiResponse.success("솔로 챌린지가 생성되었습니다.", challengeId);
    }

    @Operation(summary = "SOLO_CHALLENGE_API_02 : 대기 중인 솔로 챌린지 조회", description = "PENDING 상태 솔로 챌린지 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/pending")
    public ApiResponse<List<ChallengeResponse.SoloChallengeRes>> getPendingSoloChallenges(
            @RequestHeader("USER-ID") Long userId // TODO: 토큰 구현시 수정
    ) {
        List<ChallengeResponse.SoloChallengeRes> result = challengeService.getPendingSoloChallenges(userId);
        return ApiResponse.success("대기 중인 솔로 챌린지 목록입니다.", result);
    }

    @Operation(summary = "SOLO_CHALLENGE_API_03 : 솔로 챌린지 참여", description = "대기 중인 개인 챌린지에 참여합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "챌린지 참여 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{challengeId}/join")
    public ApiResponse<ChallengeResponse.ChallengeMateRes> joinSoloChallenge(
            @RequestHeader("USER-ID") Long userId, // TODO: 토큰 구현시 수정
            @PathVariable Long challengeId) {
        ChallengeResponse.ChallengeMateRes response = challengeService.joinSoloChallenge(challengeId, userId);
        return ApiResponse.success("솔로 챌린지 참여가 완료되었습니다.", response);
    }

}
