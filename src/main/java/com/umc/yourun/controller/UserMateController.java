package com.umc.yourun.controller;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.UserMate;
import com.umc.yourun.dto.user.UserResponseDTO;
import com.umc.yourun.service.UserMateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
        try{
            userMateService.addmate(accessToken, mateId);
        }catch(Exception e){
            return ApiResponse.error(e.getMessage(), INVALID_INPUT_VALUE,false);
        }
        return ApiResponse.success("메이트 추가에 성공했습니다.", true);
    }

    @GetMapping("/mates")
    @ResponseBody
    public ApiResponse<List<UserResponseDTO.userMateInfo>> getMates(@RequestHeader("Authorization") String accessToken){
        List<UserResponseDTO.userMateInfo> userMateList = new ArrayList<>();
        try {
            userMateList = userMateService.getUserMates(accessToken);
        }catch (Exception e){
            return ApiResponse.error(e.getMessage(), INVALID_INPUT_VALUE,null);
        }
        return ApiResponse.success("메이트 목록 조회에 성공했습니다.",userMateList);
    }

    @DeleteMapping("/mates/{mateId}")
    @ResponseBody
    public ApiResponse<Boolean> deleteMate(@RequestHeader("Authorization") String accessToken, @PathVariable Long mateId){
        try {
            userMateService.deleteMate(accessToken, mateId);
        }catch (Exception e) {
            return ApiResponse.error(e.getMessage(), INVALID_INPUT_VALUE,false);
        }
        return ApiResponse.success("메이트 삭제에 성공했습니다.", true);
    }

    @GetMapping("/mates/recommend")
    @ResponseBody
    public ApiResponse<List<UserResponseDTO.userMateInfo>> recommendMates(@RequestHeader("Authorization") String accessToken){
        List<UserResponseDTO.userMateInfo> mateInfos = new ArrayList<>();
        try {
            mateInfos = userMateService.recommendFiveMates(accessToken);
        }catch (Exception e){
            ApiResponse.error(e.getMessage(),INVALID_INPUT_VALUE, null);
        }
        return ApiResponse.success("랜덤 메이트 추천에 성공했습니다.", mateInfos);
    }
}
