package com.umc.yourun.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.converter.RunningDataConverter;
import com.umc.yourun.domain.RunningData;
import com.umc.yourun.domain.enums.RunningDataStatus;
import com.umc.yourun.dto.runningdata.RunningDataRequestDTO;
import com.umc.yourun.dto.runningdata.RunningDataResponseDTO;
import com.umc.yourun.service.RunningService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

	@GetMapping("/{years}/{months}")
	public ApiResponse<List<RunningDataResponseDTO.RunningDataInfo>> getRunningDataMonthly(@PathVariable @Valid @Min(value = 2025,message = "2025년 이후부터 조회 가능합니다.") int years,
																											@PathVariable @Valid @Min(value = 1,message = "1월부터 12월사이의 값만 조회가능합니다.") @Max(value = 12,message = "1월부터 12월사이의 값만 조회가능합니다.") int months) {

		List<RunningData> runningDataList = runningService.getRunningDataMonthly(years, months);
		return ApiResponse.success("특정 월/일 러닝 데이터 조회 성공", RunningDataConverter.toRunningDataRes(runningDataList));
	}

	@GetMapping("/{id}")
	public ApiResponse<RunningDataResponseDTO.RunningDataInfo> getRunningData(@PathVariable Long id) {
		RunningData runningData = runningService.getRunningDataById(id);
		return ApiResponse.success("러닝 데이터 조회 성공", RunningDataConverter.toRunningDataRes(runningData));
	}

	@PatchMapping("/{id}")
	public ApiResponse<RunningDataResponseDTO.RunningDataInfo> deleteRunningData(@PathVariable Long id) {
		RunningData runningData = runningService.updateRunningData(id, RunningDataStatus.DELETED);
		return ApiResponse.success("러닝 데이터 삭제 성공", RunningDataConverter.toRunningDataRes(runningData));
	}
}
