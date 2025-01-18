package com.umc.yourun.converter;

import com.umc.yourun.domain.RunningData;
import com.umc.yourun.domain.User;
import com.umc.yourun.dto.runningdata.RunningDataRequestDTO;
import com.umc.yourun.dto.runningdata.RunningDataResponseDTO;

public class RunningDataConverter {
	public static RunningData toRunningData(RunningDataRequestDTO.CreateRunningDataReq request, Integer totalTime,
		User user) {
		return RunningData.builder()
			//TODO: 유저 기능 완료 후 수정 필요
			.user(user)
			.totalDistance(request.totalDistance())
			.startTime(request.startTime())
			.endTime(request.endTime())
			.totalTime(totalTime)
			.build();
	}

	public static RunningDataResponseDTO.createRunningData toCreateRunningDataRes(RunningData runningData) {
		return RunningDataResponseDTO.createRunningData.builder()
			.id(runningData.getId())
			.startTime(runningData.getStartTime())
			.endTime(runningData.getEndTime())
			.totalDistance(runningData.getTotalDistance())
			.totalTime(runningData.getTotalTime())
			.build();
	}
}
