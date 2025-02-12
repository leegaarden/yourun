package com.umc.yourun.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.config.exception.ErrorResponse;
import com.umc.yourun.converter.UserConverter;
import com.umc.yourun.domain.User;
import com.umc.yourun.dto.user.UserRequestDTO;
import com.umc.yourun.dto.user.UserResponseDTO;
import com.umc.yourun.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
public class MypageRestController {
	private final UserService userService;


	@Operation(summary = "MYPAGE_API_01 : 마이런(마이페이지) 조회", description = "로그인된 사용자의 정보를 조회합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "마이런(마이페이지) 요청 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@GetMapping
	public ApiResponse<UserResponseDTO.userInfo> getMypage(@RequestHeader(value = "Authorization") String accessToken) {
		User newUser = userService.getUserInfo(accessToken);
		return ApiResponse.success("마이런(마이페이지) 조회 성공", UserConverter.toUserInfo(newUser));
	}

	@PatchMapping
	public ApiResponse<UserResponseDTO.userInfo> updateUser(@RequestHeader(value = "Authorization") String accessToken,
		@RequestBody UserRequestDTO.UpdateDto request) {
		User updatedUser = userService.updateUserInfo(accessToken,request);
		return ApiResponse.success("마이런(마이페이지) 조회 성공", UserConverter.toUserInfo(updatedUser));
	}
}
