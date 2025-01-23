package com.umc.yourun.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.GeneralException;
import com.umc.yourun.config.exception.custom.RunningException;
import com.umc.yourun.converter.RunningDataConverter;
import com.umc.yourun.domain.RunningData;
import com.umc.yourun.domain.User;
import com.umc.yourun.dto.runningdata.RunningDataRequestDTO;
import com.umc.yourun.repository.RunningDataRepository;
import com.umc.yourun.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RunningServiceImpl implements RunningService{
	private final RunningDataRepository runningDataRepository;
	private final UserRepository userRepository;
	private final UserService UserService;

	@Override
	@Transactional
	public RunningData createRunningData(RunningDataRequestDTO.@Valid CreateRunningDataReq request) {
		Integer totalTime=calculateTotalTime(request.startTime(),request.endTime());
		if(totalTime<0) {
			throw new RunningException(ErrorCode.INVALID_END_TIME);
		}
		User user=userRepository.findById(request.userId()).orElseThrow(()->new RunningException(ErrorCode.USER_NOT_FOUND));
		RunningData runningData= RunningDataConverter.toRunningData(request,totalTime,user);
		return runningDataRepository.save(runningData);
	}

	@Override
	public List<RunningData> getRunningDataMonthly(int years, int months) {
		//TODO: 토큰에서 가져오기
		User user=userRepository.findById(1L).orElseThrow(()->new RunningException(ErrorCode.USER_NOT_FOUND));
		LocalDateTime startDateTime = LocalDateTime.of(years, months, 1, 0, 0, 0);
		LocalDateTime endDateTime = startDateTime.plusMonths(1);
		return runningDataRepository.findByStartTimeBetweenAndUser(startDateTime,endDateTime,user);
	}

	private Integer calculateTotalTime(LocalDateTime startTime, LocalDateTime endTime) {
		return (int) (endTime.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC));
	}
}
