package com.umc.yourun.controller;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.ErrorResponse;
import com.umc.yourun.dto.challenge.ChallengeResponse;
import com.umc.yourun.dto.user.UserRequestDTO;
import com.umc.yourun.service.UserService;
import com.umc.yourun.service.challenge.SoloChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.umc.yourun.config.exception.ErrorCode.INVALID_INPUT_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;
    private final SoloChallengeService soloChallengeService;

    @PostMapping("")
    public ApiResponse join(@Valid @RequestBody UserRequestDTO.JoinDto user){
        try {
            userService.joinMember(user);
        }catch (Exception e){
            return ApiResponse.error(e.getMessage(), INVALID_INPUT_VALUE,false);
        }
        return ApiResponse.success("회원가입에 성공했습니다.", true);
    }
    @PatchMapping("")
    public ApiResponse<Boolean> delete(@RequestHeader("Authorization") String acesstoken){

        return ApiResponse.success("회원의 상태가 비활성화 상태가 되었습니다. 3일 안에 회원 탈퇴를 취소하지 않으면 계정을 복구할 수 없습니다.", userService.deleteUser(acesstoken));
    }

    @PostMapping("/login")
    public ApiResponse<Map<String,String>> getAccessToken(@Valid @RequestBody UserRequestDTO.LoginDto loginDto){
        Map<String,String> token;
        try {
            token = userService.login(loginDto);
        }catch (Exception e){
            return ApiResponse.error(e.getMessage(), INVALID_INPUT_VALUE,null);
        }
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
            @RequestHeader(value = "Authorization") String accessToken) {
        ChallengeResponse.HomeChallengeRes response = soloChallengeService.getUserChallenges(accessToken);
        return ApiResponse.success("홈 화면 : 사용자와 관련된 챌린지 정보입니다.", response);
    }

    @PostMapping("/duplicate")
    public ApiResponse<Boolean> duplicate(@RequestParam String email){
        if(userService.duplicateUserCheck(email)) {
            return ApiResponse.success("사용 가능한 이메일입니다", true);
        }else{
            return ApiResponse.error("이미 존재하는 이메일입니다.", INVALID_INPUT_VALUE,false);
        }
    }

    @PostMapping("/check-nickname")
    public ApiResponse<Boolean> checkNickname(@RequestParam String nickname){
        if(userService.checkUserNickname(nickname)) {
            return ApiResponse.success("사용 가능한 닉네임입니다", true);
        }else{
            return ApiResponse.error("이미 존재하는 닉네임입니다.", INVALID_INPUT_VALUE,false);
        }
    }
}
