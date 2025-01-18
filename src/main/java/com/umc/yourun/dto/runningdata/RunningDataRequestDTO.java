package com.umc.yourun.dto.runningdata;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public class RunningDataRequestDTO() {
	@Schema(title = "RUNNING_REQ_01 : 러닝 결과 정보 생성 요청")
	public record CreateRunningDataReq(
		@Schema(description = "시작 시간", example = "2025-01-17T15:00:00")
		LocalDateTime startTime,
		@Schema(description = "종료 시간", example = "2025-01-17T15:30:00")
		LocalDateTime endTime,
		@Schema(description = "거리", example = "5000")
		Long totalDistance,
		@Schema(description = "소모 칼로리", example = "500")
		Long calories
	) {}
}
