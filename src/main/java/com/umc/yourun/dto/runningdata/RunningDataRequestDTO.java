package com.umc.yourun.dto.runningdata;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RunningDataRequestDTO {

	@Schema(title = "RUNNING_REQ_01 : 러닝 결과 정보 생성 요청")
	public record CreateRunningDataReq(
		//TODO: 토큰에서 가져오기
		@Schema(description = "user ID", example = "1")
		@NotNull(message = "user ID는 필수 입력 값입니다.")
		Long userId,
		@Schema(description = "목표 시간")
		@Min(value = 0, message = "목표 시간은 0 이상의 값이어야 합니다.")
		@Max(value = 60, message = "목표 시간은 60 이하의 값이어야 합니다.")
		@NotNull(message = "목표 시간은 필수 입력 값입니다.")
		Integer targetTime,
		@Schema(description = "시작 시간", example = "2025-01-17T15:00:00")
		@NotNull(message = "시작 시간은 필수 입력 값입니다.")
		LocalDateTime startTime,
		@Schema(description = "종료 시간", example = "2025-01-17T15:30:00")
		@NotNull(message = "종료 시간은 필수 입력 값입니다.")
		LocalDateTime endTime,
		@Schema(description = "총 거리", example = "5000")
		@Min(value = 0, message = "총 거리는 0 이상의 값이어야 합니다.")
		Integer totalDistance
	) {}
}
