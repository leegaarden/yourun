package com.umc.yourun.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.converter.RunningDataConverter;
import com.umc.yourun.domain.RunningData;
import com.umc.yourun.dto.runningdata.RunningDataRequestDTO;
import com.umc.yourun.dto.runningdata.RunningDataResponseDTO;
import com.umc.yourun.service.RunningService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/runnings")
@Tag(name = "Running", description = "러닝 API")
public class RunningRestController {

	private final RunningService runningService;

	@PostMapping
	public ApiResponse<RunningDataResponseDTO.createRunningData> createRunningData(@RequestBody @Valid RunningDataRequestDTO.CreateRunningDataReq request) {
		RunningData runningData = runningService.createRunningData(request);
		return ApiResponse.success("러닝 결과 정보 생성 성공", RunningDataConverter.toCreateRunningDataRes(runningData));
	}
}
