package com.umc.yourun.controller;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.config.exception.ErrorResponse;
import com.umc.yourun.dto.challenge.ChallengeRequest;
import com.umc.yourun.dto.challenge.ChallengeResponse;
import com.umc.yourun.service.challenge.ChallengeService;
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
            @RequestHeader("USER-ID") Long userId, // TODO: 토큰 구현시 수정
            @RequestBody @Valid ChallengeRequest.CreateCrewChallengeReq request) {
        Long challengeId = challengeService.createCrewChallenge(request, userId);
        return ApiResponse.success("크루 챌린지가 생성되었습니다.", challengeId);
    }

    @Operation(summary = "CHALLENGE_API_02 : 솔로 챌린지 생성", description = "새로운 솔로 챌린지를 생성합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "챌린지 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/solo")
    public ApiResponse<Long> createSoloChallenge(
            @RequestHeader("USER-ID") Long userId, // TODO: 토큰 구현시 수정
            @RequestBody @Valid ChallengeRequest.CreateSoloChallengeReq request) {
        Long challengeId = challengeService.createSoloChallenge(request, userId);
        return ApiResponse.success("개인 챌린지가 생성되었습니다.", challengeId);
    }

    @Operation(summary = "CHALLENGE_API_03 : 대기 중인 크루 챌린지 조회", description = "PENDING 상태인 크루 챌린지 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/crew/pending")
    public ApiResponse<List<ChallengeResponse.CrewChallengeStatusRes>> getPendingCrewChallenges(
            @RequestHeader("USER-ID") Long userId // TODO: 토큰 구현시 수정
    ) {
        List<ChallengeResponse.CrewChallengeStatusRes> result = challengeService.getPendingCrewChallenges(userId);
        return ApiResponse.success("대기 중인 크루 챌린지 목록입니다.", result);
    }

    @Operation(summary = "CHALLENGE_API_04 : 진행 중인 크루 챌린지 조회", description = "IN_PROGRESS 상태인 크루 챌린지 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/crew/in-progress")
    public ApiResponse<List<ChallengeResponse.CrewChallengeStatusRes>> getInProgressCrewChallenges(
            @RequestHeader("USER-ID") Long userId // TODO: 토큰 구현시 수정
    ) {
        List<ChallengeResponse.CrewChallengeStatusRes> result = challengeService.getPendingCrewChallenges(userId);
        return ApiResponse.success("진행 중인 크루 챌린지 목록입니다.", result);
    }

    @Operation(summary = "CHALLENGE_API_05 : 대기 중인 솔로 챌린지 조회", description = "PENDING 상태인 솔로 챌린지 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/solo/pending")
    public ApiResponse<List<ChallengeResponse.SoloChallengeStatusRes>> getPendingSoloChallenges(
            @RequestHeader("USER-ID") Long userId // TODO: 토큰 구현시 수정
    ) {
        List<ChallengeResponse.SoloChallengeStatusRes> result = challengeService.getPendingSoloChallenges(userId);
        return ApiResponse.success("대기 중인 솔로 챌린지 목록입니다.", result);
    }

    @Operation(summary = "CHALLENGE_API_06 : 진행 중인 솔로 챌린지 조회", description = "IN_PROGRESS 상태인 솔로 챌린지 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/solo/in-progress")
    public ApiResponse<List<ChallengeResponse.SoloChallengeStatusRes>> getInProgressSoloChallenges(
            @RequestHeader("USER-ID") Long userId // TODO: 토큰 구현시 수정
    ) {
        List<ChallengeResponse.SoloChallengeStatusRes> result = challengeService.getInProgressSoloChallenges(userId);
        return ApiResponse.success("진행 중인 솔로 챌린지 목록입니다.", result);
    }

    @Operation(summary = "CHALLENGE_API_07 : 솔로 챌린지 참여", description = "대기 중인 개인 챌린지에 참여합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "챌린지 참여 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/solo/{challengeId}/join")
    public ApiResponse<ChallengeResponse.ChallengeMateRes> joinSoloChallenge(
            @RequestHeader("USER-ID") Long userId, // TODO: 토큰 구현시 수정
            @PathVariable Long challengeId) {
        ChallengeResponse.ChallengeMateRes response = challengeService.joinSoloChallenge(challengeId, userId);
        return ApiResponse.success("개인 챌린지 참여가 완료되었습니다.", response);
    }

    @PostMapping("/crew/{challengeId}/join")
    @Operation(summary = "CHALLENGE_API_08 : 크루 챌린지 참여", description = "대기 중인 크루 챌린지에 참여합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "챌린지 참여 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ApiResponse<ChallengeResponse.CrewChallengeMateRes> joinCrewChallenge(
            @RequestHeader("USER-ID") Long userId, // TODO: 토큰 구현시 수정
            @PathVariable Long challengeId) {
        ChallengeResponse.CrewChallengeMateRes response = challengeService.joinCrewChallenge(challengeId, userId);
        return ApiResponse.success("크루 챌린지 참여가 완료되었습니다.", response);
    }
}
