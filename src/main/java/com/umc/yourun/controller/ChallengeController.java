package com.umc.yourun.controller;

//import io.swagger.v3.oas.annotations.responses.ApiResponse;
import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.dto.challenge.ChallengeRequest;
import com.umc.yourun.dto.challenge.ChallengeResponse;
import com.umc.yourun.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/challenges")
@Tag(name = "Challenge", description = "챌린지 API")
public class ChallengeController {

    private final ChallengeService challengeService;

    @Operation(summary = "크루 챌린지 생성", description = "새로운 크루 챌린지를 생성합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "챌린지 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/crew")
    public ApiResponse<ChallengeResponse.CrewChallengeResult> createCrewChallenge(
            @RequestBody @Valid ChallengeRequest.CrewChallengeCreateReq request) {
        ChallengeResponse.CrewChallengeResult result = challengeService.createCrewChallenge(request);
        return ApiResponse.success("크루 챌린지가 생성되었습니다.", result);
    }

    @Operation(summary = "개인 챌린지 생성", description = "새로운 개인 챌린지를 생성합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "챌린지 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/solo")
    public ApiResponse<ChallengeResponse.SoloChallengeResult> createSoloChallenge(
            @RequestBody @Valid ChallengeRequest.SoloChallengeCreateReq request) {
        ChallengeResponse.SoloChallengeResult result = challengeService.createSoloChallenge(request);
        return ApiResponse.success("개인 챌린지가 생성되었습니다.", result);
    }
}