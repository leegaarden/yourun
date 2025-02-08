package com.umc.yourun.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import com.umc.yourun.domain.enums.ChallengeResult;
import com.umc.yourun.domain.mapping.UserSoloChallenge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc.yourun.config.JwtTokenProvider;
import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.custom.RunningException;
import com.umc.yourun.converter.RunningDataConverter;
import com.umc.yourun.domain.RunningData;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.ChallengeStatus;
import com.umc.yourun.domain.enums.RunningDataStatus;
import com.umc.yourun.dto.runningdata.RunningDataRequestDTO;
import com.umc.yourun.repository.RunningDataRepository;
import com.umc.yourun.repository.UserCrewChallengeRepository;
import com.umc.yourun.repository.UserSoloChallengeRepository;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import javax.swing.text.html.Option;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RunningService {

	private final RunningDataRepository runningDataRepository;
	private final UserCrewChallengeRepository userCrewChallengeRepository;
	private final UserSoloChallengeRepository userSoloChallengeRepository;
	private final JwtTokenProvider jwtTokenProvider;

	@Transactional
	public RunningData createRunningData(String accessToken,RunningDataRequestDTO.@Valid CreateRunningDataReq request) {
		User user = jwtTokenProvider.getUserByToken(accessToken);
		Integer totalTime=calculateTotalTime(request.startTime(),request.endTime());
		if(totalTime<0) {
			throw new RunningException(ErrorCode.INVALID_END_TIME);
		}
		RunningData runningData= RunningDataConverter.toRunningData(request,totalTime,user);
		return runningDataRepository.save(runningData);
	}

	public List<RunningData> getRunningDataMonthly(String accessToken,int years, int months) {
		User user = jwtTokenProvider.getUserByToken(accessToken);
		LocalDateTime startDateTime = LocalDateTime.of(years, months, 1, 0, 0, 0);
		LocalDateTime endDateTime = startDateTime.plusMonths(1);
		return runningDataRepository.findByStatusAndStartTimeBetweenAndUser(RunningDataStatus.ACTIVE,startDateTime,endDateTime,user);
	}

	public RunningData getRunningDataById(Long id) {
		return runningDataRepository.findByIdAndStatus(id,RunningDataStatus.ACTIVE).orElseThrow(()->new RunningException(ErrorCode.RUNNING_DATA_NOT_FOUND));
	}

	@Transactional
	public RunningData updateRunningData(Long id, RunningDataStatus status) {
		RunningData runningData=runningDataRepository.findByIdAndStatus(id,RunningDataStatus.ACTIVE).orElseThrow(()->new RunningException(ErrorCode.RUNNING_DATA_NOT_FOUND));
		runningData.setStatus(status);
		return runningData;
	}

	private Integer calculateTotalTime(LocalDateTime startTime, LocalDateTime endTime) {
		return (int) (endTime.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC));
	}

	public Boolean isSoloChallengeInProgress(String accessToken) {
		User user = jwtTokenProvider.getUserByToken(accessToken);

		// 가장 최근의 챌린지를 조회하고, 상태가 IN_PROGRESS인 것만 필터링
		Optional<UserSoloChallenge> userSoloChallenge = userSoloChallengeRepository
				.findFirstByUserIdAndSoloChallenge_ChallengeStatusInOrderByCreatedAtDesc(
						user.getId(),
						List.of(ChallengeStatus.IN_PROGRESS)
				);

		// 챌린지가 존재하고, 유저의 챌린지 결과가 IN_PROGRESS이며, 현재 시간이 시작일 이후인지 확인
		return userSoloChallenge.isPresent() &&
				userSoloChallenge.get().getChallengeResult() == ChallengeResult.IN_PROGRESS &&
				LocalDateTime.now().isAfter(userSoloChallenge.get().getSoloChallenge().getStartDate());
	}

	public Boolean isCrewChallengeInProgress(String accessToken) {
		User user = jwtTokenProvider.getUserByToken(accessToken);
		return userCrewChallengeRepository.existsByUserIdAndCrewChallenge_ChallengeStatusIn(user.getId(),
			List.of(ChallengeStatus.IN_PROGRESS));
	}

}
