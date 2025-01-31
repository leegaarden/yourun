package com.umc.yourun.controller;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.UserMate;
import com.umc.yourun.dto.user.UserResponseDTO;
import com.umc.yourun.service.UserMateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.umc.yourun.config.exception.ErrorCode.INVALID_INPUT_VALUE;

@Controller
@RequestMapping("api/v1/users")
public class UserMateController {
    private static UserMateService userMateService;

    @Autowired
    public UserMateController(UserMateService userMateService) {
        this.userMateService = userMateService;
    }

    @PostMapping("/mates/{mateId}")
    @ResponseBody
    public ApiResponse<Boolean> addMate(@RequestHeader("Authorization") String accessToken, @PathVariable Long mateId){
        System.out.println(accessToken);
        if(userMateService.addmate(accessToken, mateId)) {
            return ApiResponse.success("메이트 추가에 성공했습니다.", true);
        }else{
            return ApiResponse.error(INVALID_INPUT_VALUE);
        }
    }

    @GetMapping("/mates")
    @ResponseBody
    public ApiResponse<List<UserResponseDTO.userMateInfo>> getMates(@RequestHeader("Authorization") String accessToken){
        List<UserResponseDTO.userMateInfo> userMateList = userMateService.getUserMates(accessToken);
        if(userMateList.isEmpty()) {
            return ApiResponse.error(INVALID_INPUT_VALUE);
        }else{
            return ApiResponse.success("메이트 목록 조회에 성공했습니다.", userMateList);
        }
    }

    @DeleteMapping("/mates/{mateId}")
    @ResponseBody
    public ApiResponse<Boolean> deleteMate(@RequestHeader("Authorization") String accessToken, @PathVariable Long mateId){
        if(userMateService.deleteMate(accessToken, mateId)) {
            return ApiResponse.success("메이트 삭제에 성공했습니다.", true);
        }else{
            return ApiResponse.error(INVALID_INPUT_VALUE);
        }
    }
}
