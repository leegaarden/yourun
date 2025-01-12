package com.umc.yourun.controller;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.service.UserMateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.umc.yourun.config.exception.ErrorCode.INVALID_INPUT_VALUE;

@Controller
@RequestMapping("/users")
public class UserMateController {
    UserMateService userMateService;

    @Autowired
    public UserMateController(UserMateService userMateService) {
        this.userMateService = userMateService;
    }

    @GetMapping("/{userId}/mates/{mateId}")
    @ResponseBody
    public ApiResponse addMate(@PathVariable Long userId,@PathVariable Long mateId){
        if(userMateService.addmate(userId, mateId).equals(true)) {
            return ApiResponse.success("메이트 추가에 성공했습니다.", true);
        }else{
            return ApiResponse.error(INVALID_INPUT_VALUE);
        }
    }
}
