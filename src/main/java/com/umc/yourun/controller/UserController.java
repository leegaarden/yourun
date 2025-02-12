package com.umc.yourun.controller;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.config.exception.ErrorResponse;
import com.umc.yourun.dto.challenge.CrewChallengeResponse;
import com.umc.yourun.dto.challenge.SoloChallengeResponse;
import com.umc.yourun.dto.user.UserRequestDTO;
import com.umc.yourun.service.UserService;
import com.umc.yourun.service.challenge.SoloChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    public ApiResponse<Boolean> delete(@RequestHeader("Authorization") String accesstoken){
        return ApiResponse.success("회원의 상태가 비활성화 상태가 되었습니다. 3일 안에 회원 탈퇴를 취소하지 않으면 계정을 복구할 수 없습니다.", userService.deleteUser(accesstoken));
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
    public ApiResponse<SoloChallengeResponse.HomeChallengeRes> getUserChallenges(
            @RequestHeader(value = "Authorization") String accessToken) {
        SoloChallengeResponse.HomeChallengeRes response = soloChallengeService.getUserChallenges(accessToken);
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

    @GetMapping("/kakao-login")
    public ApiResponse<Map<String,String>> kakaoLogin(@AuthenticationPrincipal OAuth2User user) {
        if (user == null) {
            return ApiResponse.error("카카오 로그인에 실패했습니다.", INVALID_INPUT_VALUE, null);
        }else {
            return ApiResponse.success("카카오 로그인에 성공했습니다.", userService.kakaoLogin(user));
        }
    }

    @PatchMapping("/initialize")
    public ApiResponse<Boolean> setUserInfo(@RequestHeader("Authorization") String accesstoken, @Valid @RequestBody UserRequestDTO.SetKakaoUserDto kakaoUserInfo) {
        try {
            userService.setKakaoUserInfo(accesstoken, kakaoUserInfo);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage(), INVALID_INPUT_VALUE, false);
        }
        return ApiResponse.success("카카오 회원 초기 정보 설정에 성공했습니다.", true);
    }

    @Operation(summary = "CHALLENGE_API_2 : 챌린지 매칭 확인", description = "유저의 솔로 및 크루 챌린지가 매칭되었는지 확인합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })

    @GetMapping("/challenges/check-matching")
    public ApiResponse<SoloChallengeResponse.CheckChallengeMatchingRes> getCheckChallengeMatching(
            @RequestHeader(value = "Authorization") String accessToken) {
        SoloChallengeResponse.CheckChallengeMatchingRes response = soloChallengeService.getCheckChallengeMatching(accessToken);
        return ApiResponse.success("유저의 솔로, 크루 챌린지가 매칭되었는지 확인합니다.", response);
    }
}
