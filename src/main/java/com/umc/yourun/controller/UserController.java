package com.umc.yourun.controller;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.config.exception.ErrorResponse;
import com.umc.yourun.dto.challenge.ChallengeResponse;
import com.umc.yourun.dto.user.UserRequestDTO;
import com.umc.yourun.service.UserService;
import com.umc.yourun.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import static com.umc.yourun.config.exception.ErrorCode.INVALID_INPUT_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;
    private final ChallengeService challengeService;

    @PostMapping("")
    public ApiResponse<Boolean> join(@RequestBody UserRequestDTO.JoinDto user){
        if(userService.joinMember(user)) {
            return ApiResponse.success("회원가입에 성공했습니다.", true);
        }else{
            return  ApiResponse.error(INVALID_INPUT_VALUE);
        }
    }
    @PatchMapping("")
    public ApiResponse<Boolean> delete(@RequestHeader("Authorization") String acesstoken){
        return ApiResponse.success("회원의 상태가 비활성화 상태가 되었습니다. 3일 안에 회원 탈퇴를 취소하지 않으면 계정을 복구할 수 없습니다.", userService.deleteUser(acesstoken));
    }

    @PostMapping("/login")
    public ApiResponse<Map<String,String>> getAccessToken(@RequestBody UserRequestDTO.LoginDto loginDto){
        Map<String,String> token = userService.login(loginDto);
        return ApiResponse.success("로그인에 성공했습니다.", token);
    }

    // 홈 화면에서 챌린지 조회 부분
    @Operation(summary = "CHALLENGE_API_1 : 홈 화면 챌린지 조회", description = "사용자가 참여중인 모든 챌린지를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/home/challenges")
    public ApiResponse<ChallengeResponse.HomeChallengeRes> getUserChallenges(
            @RequestHeader("USER-ID") Long userId) {
        ChallengeResponse.HomeChallengeRes response = challengeService.getUserChallenges(userId);
        return ApiResponse.success("홈 화면 : 사용자와 관련된 챌린지 정보입니다.", response);
    }
}
