package com.umc.yourun.controller;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.ErrorResponse;
import com.umc.yourun.config.exception.GeneralException;
import com.umc.yourun.domain.User;
import com.umc.yourun.dto.challenge.ChallengeRequest;
import com.umc.yourun.dto.challenge.ChallengeResponse;
import com.umc.yourun.service.UserService;
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
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/challenges")
@Tag(name = "Challenge", description = "챌린지 API")
public class ChallengeRestController {

    private final ChallengeService challengeService;
    private final UserService userService;

    @Operation(summary = "CHALLENGE_API_01 : 크루 챌린지 생성", description = "새로운 크루 챌린지를 생성합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "챌린지 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/crew")
    public ApiResponse<Long> createCrewChallenge(
            // @RequestHeader("Authorization") String token,
            @RequestHeader("USER-ID") Long userId, // TODO: 토큰 구현시 수정
            @RequestBody @Valid ChallengeRequest.CreateCrewChallengeReq request) {

        Long challengeId = challengeService.createCrewChallenge(request, userId );
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
            // @RequestHeader("Authorization") String token,
            @RequestHeader("USER-ID") Long userId, // TODO: 토큰 구현시 수정
            @RequestBody @Valid ChallengeRequest.CreateSoloChallengeReq request) {
        Long challengeId = challengeService.createSoloChallenge(request, userId);
        return ApiResponse.success("솔로 챌린지가 생성되었습니다.", challengeId);
    }

    @Operation(summary = "CHALLENGE_API_03 : 대기 중인 크루 챌린지 조회", description = "PENDING 상태인 크루 챌린지 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/crew/pending")
    public ApiResponse<List<ChallengeResponse.CrewChallengeStatusRes>> getPendingCrewChallenges(
            // @RequestHeader("Authorization") String token
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
        return ApiResponse.success("솔로 챌린지 참여가 완료되었습니다.", response);
    }

    @Operation(summary = "CHALLENGE_API_08 : 크루 챌린지 참여", description = "대기 중인 크루 챌린지에 참여합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "챌린지 참여 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/crew/{challengeId}/join")
    public ApiResponse<ChallengeResponse.CrewChallengeMateRes> joinCrewChallenge(
            @RequestHeader("USER-ID") Long userId, // TODO: 토큰 구현시 수정
            @PathVariable Long challengeId) {
        ChallengeResponse.CrewChallengeMateRes response = challengeService.joinCrewChallenge(challengeId, userId);
        return ApiResponse.success("크루 챌린지 참여가 완료되었습니다.", response);
    }

    @Operation(summary = "CHALLENGE_API_09 : 크루 챌린지 매칭 조회", description = "사용자가 현재 참여중인 크루 챌린지의 매칭 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "참여중인 크루 챌린지가 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/crew/match")
    public ApiResponse<ChallengeResponse.CrewMatchingRes> getCrewMatch(
            @RequestHeader("USER-ID") Long userId) {
        ChallengeResponse.CrewMatchingRes response = challengeService.getCrewMatch(userId);
        return ApiResponse.success("크루 챌린지 매칭 정보입니다.", response);
    }

    @Operation(summary = "CHALLENGE_API_10 : 홈 화면 챌린지 조회", description = "사용자가 참여중인 모든 챌린지를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/home")
    public ApiResponse<ChallengeResponse.HomeChallengeRes> getUserChallenges(
            @RequestHeader("USER-ID") Long userId) {
        ChallengeResponse.HomeChallengeRes response = challengeService.getUserChallenges(userId);
        return ApiResponse.success("홈 화면 : 사용자와 관련된 챌린지 정보입니다.", response);
    }
}
