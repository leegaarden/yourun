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
@RequestMapping("/api/v1/challenges/crew")
@Tag(name = "CrewChallenge", description = "크루 챌린지 API")
public class CrewChallengeRestController {

    private final ChallengeService challengeService;

    @Operation(summary = "CREW_CHALLENGE_API_01 : 크루 챌린지 생성", description = "새로운 크루 챌린지를 생성합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "챌린지 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("")
    public ApiResponse<Long> createCrewChallenge(
            // @RequestHeader("Authorization") String token,
            @RequestHeader("USER-ID") Long userId, // TODO: 토큰 구현시 수정
            @RequestBody @Valid ChallengeRequest.CreateCrewChallengeReq request) {

        Long challengeId = challengeService.createCrewChallenge(request, userId );
        return ApiResponse.success("크루 챌린지가 생성되었습니다.", challengeId);
    }

    @Operation(summary = "CREW_CHALLENGE_API_02 : 대기 중인 크루 챌린지 조회", description = "PENDING 상태인 크루 챌린지 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/pending")
    public ApiResponse<List<ChallengeResponse.CrewChallengeStatusRes>> getPendingCrewChallenges(
            // @RequestHeader("Authorization") String token
            @RequestHeader("USER-ID") Long userId // TODO: 토큰 구현시 수정
    ) {
        List<ChallengeResponse.CrewChallengeStatusRes> result = challengeService.getPendingCrewChallenges(userId);
        return ApiResponse.success("대기 중인 크루 챌린지 목록입니다.", result);
    }

    @Operation(summary = "CREW_CHALLENGE_API_03 : 진행 중인 크루 챌린지 조회", description = "IN_PROGRESS 상태인 크루 챌린지 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/in-progress")
    public ApiResponse<List<ChallengeResponse.CrewChallengeStatusRes>> getInProgressCrewChallenges(
            @RequestHeader("USER-ID") Long userId // TODO: 토큰 구현시 수정
    ) {
        List<ChallengeResponse.CrewChallengeStatusRes> result = challengeService.getPendingCrewChallenges(userId);
        return ApiResponse.success("진행 중인 크루 챌린지 목록입니다.", result);
    }

    @Operation(summary = "CREW_CHALLENGE_API_04 : 크루 챌린지 참여", description = "대기 중인 크루 챌린지에 참여합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "챌린지 참여 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{challengeId}/join")
    public ApiResponse<ChallengeResponse.CrewChallengeMateRes> joinCrewChallenge(
            @RequestHeader("USER-ID") Long userId, // TODO: 토큰 구현시 수정
            @PathVariable Long challengeId) {
        ChallengeResponse.CrewChallengeMateRes response = challengeService.joinCrewChallenge(challengeId, userId);
        return ApiResponse.success("크루 챌린지 참여가 완료되었습니다.", response);
    }

    @Operation(summary = "CREW_CHALLENGE_API_05 : 크루 챌린지 매칭 조회", description = "사용자가 현재 참여중인 크루 챌린지의 매칭 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "참여중인 크루 챌린지가 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/match")
    public ApiResponse<ChallengeResponse.CrewMatchingRes> getCrewMatch(
            @RequestHeader("USER-ID") Long userId) {
        ChallengeResponse.CrewMatchingRes response = challengeService.getCrewMatch(userId);
        return ApiResponse.success("크루 챌린지 매칭 정보입니다.", response);
    }
}
