package com.umc.yourun.dto.runningdata;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public class RunningDataResponseDTO{
	@Schema(title = "RUNNING_RES_01 : 러닝 결과 정보 생성 응답 DTO")
	public record createRunningData(

		@Schema(description = "러닝 결과 ID", example = "1")
		Long challengeId,

		@Schema(description = "사용자 이름", example = "홍길동")
		String userName,

		@Schema(description = "시작 시간", example = "2025-01-17T15:00:00")
		LocalDateTime startTime,

		@Schema(description = "종료 시간", example = "2025-01-17T15:30:00")
		LocalDateTime endTime,

		@Schema(description = "총 거리 (m) ", example = "5000")
		Long totalDistance,

		@Schema(description = "총 시간 (s) ", example = "500000")
		Long totalTime,

		@Schema(description = "소모 칼로리", example = "500")
		Long calories

	) {}
}
