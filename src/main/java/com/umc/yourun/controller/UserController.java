package com.umc.yourun.controller;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.config.exception.custom.UserException;
import com.umc.yourun.dto.user.UserRequestDTO;
import com.umc.yourun.service.TokenService;
import com.umc.yourun.service.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.umc.yourun.config.exception.ErrorCode.INVALID_INPUT_VALUE;
import static com.umc.yourun.config.exception.ErrorCode.INVALID_USER_INCONSISTENCY;

@RestController
@RequestMapping("/users")
public class UserController {
    private static UserService userService;
    private static TokenService tokenService;

    @Autowired
    public UserController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @PostMapping("")
    public ApiResponse<Boolean> join(@RequestBody UserRequestDTO.JoinDto user){
        if(userService.joinMember(user)) {
            return ApiResponse.success("회원가입에 성공했습니다.", true);
        }else{
            return  ApiResponse.error(INVALID_INPUT_VALUE);
        }
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, String>> login(@RequestBody UserRequestDTO.LoginDto loginDto){
        Map<String, String> accessToken = new HashMap<>();
        if(!userService.login(loginDto)){
            return ApiResponse.error(INVALID_USER_INCONSISTENCY);
        }
        accessToken.put("access_token", tokenService.getAccessToken(loginDto.email(), loginDto.password()));
        return ApiResponse.success("로그인에 성공했습니다.", accessToken);
    }

}
