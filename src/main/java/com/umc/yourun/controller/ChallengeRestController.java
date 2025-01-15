package com.umc.yourun.controller;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.config.exception.ErrorResponse;
import com.umc.yourun.domain.CrewChallenge;
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
@RequestMapping("/api/v1/challenges")
@Tag(name = "Challenge", description = "챌린지 API")
public class ChallengeRestController {

    private final ChallengeService challengeService;

    @Operation(summary = "CHALLENGE_API_01 : 크루 챌린지 생성", description = "새로운 크루 챌린지를 생성합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "챌린지 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/crew")
    public ApiResponse<Long> createCrewChallenge(
            @RequestBody @Valid ChallengeRequest.CreateCrewChallengeReq request) {
        Long challengeId = challengeService.createCrewChallenge(request);
        return ApiResponse.success("크루 챌린지가 생성되었습니다.", challengeId);
    }

    @Operation(summary = "CHALLENGE_API_02 : 개인 챌린지 생성", description = "새로운 개인 챌린지를 생성합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "챌린지 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/solo")
    public ApiResponse<Long> createSoloChallenge(
            @RequestBody @Valid ChallengeRequest.CreateSoloChallengeReq request) {
        Long challengeId = challengeService.createSoloChallenge(request);
        return ApiResponse.success("개인 챌린지가 생성되었습니다.", challengeId);
    }

    @Operation(summary = "CHALLENGE_API_03 : 대기 중인 크루 챌린지 조회", description = "PENDING 상태인 크루 챌린지 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/crew/pending")
    public ApiResponse<List<ChallengeResponse.CrewChallengeStatusRes>> getPendingCrewChallenges() {
        List<ChallengeResponse.CrewChallengeStatusRes> result = challengeService.getPendingCrewChallenges();
        return ApiResponse.success("대기 중인 크루 챌린지 목록입니다.", result);
    }

    @Operation(summary = "CHALLENGE_API_04 : 진행 중인 크루 챌린지 조회", description = "IN_PROGRESS 상태인 크루 챌린지 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/crew/in-progress")
    public ApiResponse<List<ChallengeResponse.CrewChallengeStatusRes>> getInProgressCrewChallenges() {
        List<ChallengeResponse.CrewChallengeStatusRes> result = challengeService.getPendingCrewChallenges();
        return ApiResponse.success("진행 중인 크루 챌린지 목록입니다.", result);
    }
}
