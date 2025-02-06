package com.umc.yourun.converter;

import java.util.List;

import com.umc.yourun.domain.RunningData;
import com.umc.yourun.domain.User;
import com.umc.yourun.dto.runningdata.RunningDataRequestDTO;
import com.umc.yourun.dto.runningdata.RunningDataResponseDTO;

public class RunningDataConverter {
	public static RunningData toRunningData(RunningDataRequestDTO.CreateRunningDataReq request, Integer totalTime,
		User user) {
		return RunningData.builder()
			.user(user)
			.targetTime(request.targetTime())
			.totalDistance(request.totalDistance())
			.startTime(request.startTime())
			.endTime(request.endTime())
			.totalTime(totalTime)
			.build();
	}

	public static RunningDataResponseDTO.RunningDataCreateRes toCreateRunningDataRes(RunningData runningData,Boolean isSoloChallengeInProgress,Boolean isCrewChallengeInProgress) {
		return RunningDataResponseDTO.RunningDataCreateRes.builder()
			.id(runningData.getId())
			.startTime(runningData.getStartTime())
			.endTime(runningData.getEndTime())
			.totalDistance(runningData.getTotalDistance())
			.totalTime(runningData.getTotalTime())
			.userName(runningData.getUser().getNickname())
			.pace(runningData.getPace())
			.isSoloChallengeInProgress(isCrewChallengeInProgress)
			.isCrewChallengeInProgress(isCrewChallengeInProgress)
			.build();
	}

	public static List<RunningDataResponseDTO.RunningDataInfo> toRunningDataRes(List<RunningData> runningDataList) {
		return runningDataList.stream()
			.map(RunningDataConverter::toRunningDataRes)
			.toList();
	}

	public static RunningDataResponseDTO.RunningDataInfo toRunningDataRes(RunningData runningData) {
		return RunningDataResponseDTO.RunningDataInfo.builder()
			.id(runningData.getId())
			.totalDistance(runningData.getTotalDistance())
			.totalTime(runningData.getTotalTime())
			.pace(runningData.getPace())
			.createdAt(runningData.getCreatedAt())
			.build();
	}
}
