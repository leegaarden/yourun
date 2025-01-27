package com.umc.yourun.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umc.yourun.apiPayload.ApiResponse;
import com.umc.yourun.config.exception.ErrorResponse;
import com.umc.yourun.converter.RunningDataConverter;
import com.umc.yourun.domain.RunningData;
import com.umc.yourun.domain.enums.RunningDataStatus;
import com.umc.yourun.dto.runningdata.RunningDataRequestDTO;
import com.umc.yourun.dto.runningdata.RunningDataResponseDTO;
import com.umc.yourun.service.RunningService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

	@Operation(summary = "RUNNING_DATA_API_01 : 크루 챌린지 생성", description = "새로운 러닝 데이터를 생성합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "러닝데이터 생성 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PostMapping
	public ApiResponse<RunningDataResponseDTO.createRunningData> createRunningData(@RequestHeader(value = "Authorization") String accessToken,
																					@RequestBody @Valid RunningDataRequestDTO.CreateRunningDataReq request) {
		RunningData runningData = runningService.createRunningData(accessToken,request);
		return ApiResponse.success("러닝 결과 정보 생성 성공", RunningDataConverter.toCreateRunningDataRes(runningData));
	}

	@Operation(summary = "RUNNING_DATA_API_01 : 특정 년/월의 모든 러닝데이터 조회(캘린더에서 사용)", description = "특정한 년/월의 모든 러닝 데이터를 조회합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "러닝데이터 조회 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@GetMapping("/{years}/{months}")
	public ApiResponse<List<RunningDataResponseDTO.RunningDataInfo>> getRunningDataMonthly(@RequestHeader(value = "Authorization") String accessToken,
																							@PathVariable @Valid @Min(value = 2025,message = "2025년 이후부터 조회 가능합니다.") int years,
																							@PathVariable @Valid @Min(value = 1,message = "1월부터 12월사이의 값만 조회가능합니다.") @Max(value = 12,message = "1월부터 12월사이의 값만 조회가능합니다.") int months) {

		List<RunningData> runningDataList = runningService.getRunningDataMonthly(accessToken,years, months);
		return ApiResponse.success("특정 월/일 러닝 데이터 조회 성공", RunningDataConverter.toRunningDataRes(runningDataList));
	}

	@Operation(summary = "RUNNING_DATA_API_03 : 러닝 데이터 조회", description = "특정 id의 러닝 데이터를 조회합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "러닝데이터 조회 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@GetMapping("/{id}")
	public ApiResponse<RunningDataResponseDTO.RunningDataInfo> getRunningData(@RequestHeader(value = "Authorization") String accessToken,
																				@PathVariable Long id) {
		RunningData runningData = runningService.getRunningDataById(id);
		return ApiResponse.success("러닝 데이터 조회 성공", RunningDataConverter.toRunningDataRes(runningData));
	}

	@Operation(summary = "RUNNING_DATA_API_04 : 러닝 데이터 삭제", description = "특정 id의 러닝 데이터를 삭제합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "러닝데이터 삭제 성공"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PatchMapping("/{id}")
	public ApiResponse<RunningDataResponseDTO.RunningDataInfo> deleteRunningData(@RequestHeader(value = "Authorization") String accessToken,
																					@PathVariable Long id) {
		RunningData runningData = runningService.updateRunningData(id, RunningDataStatus.DELETED);
		return ApiResponse.success("러닝 데이터 삭제 성공", RunningDataConverter.toRunningDataRes(runningData));
	}
}
