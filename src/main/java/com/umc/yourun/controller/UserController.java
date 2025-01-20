package com.umc.yourun.controller;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.dto.user.UserRequestDTO;
import com.umc.yourun.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.umc.yourun.config.exception.ErrorCode.INVALID_INPUT_VALUE;
import static com.umc.yourun.config.exception.ErrorCode.INVALID_USER_INCONSISTENCY;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
    public ApiResponse<Map<String,String>> getAccessToken(@RequestBody UserRequestDTO.LoginDto loginDto){
        Map<String,String> token = userService.login(loginDto);
        return ApiResponse.success("로그인에 성공했습니다.", token);
    }
}
