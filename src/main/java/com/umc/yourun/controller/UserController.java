package com.umc.yourun.controller;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.dto.user.UserRequestDTO;
import com.umc.yourun.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import static com.umc.yourun.config.exception.ErrorCode.INVALID_INPUT_VALUE;

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
