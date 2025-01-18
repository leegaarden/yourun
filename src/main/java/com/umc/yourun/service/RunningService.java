package com.umc.yourun.service;

import com.umc.yourun.domain.RunningData;
import com.umc.yourun.dto.runningdata.RunningDataRequestDTO;

import jakarta.validation.Valid;

public interface RunningService {
	RunningData createRunningData(RunningDataRequestDTO.@Valid CreateRunningDataReq request);
}
