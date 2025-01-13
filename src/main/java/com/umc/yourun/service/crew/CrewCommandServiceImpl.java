package com.umc.yourun.service.crew;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc.yourun.converter.CrewConverter;
import com.umc.yourun.converter.UserCrewConverter;
import com.umc.yourun.domain.Crew;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.mapping.UserCrew;
import com.umc.yourun.dto.CrewRequestDTO;
import com.umc.yourun.repository.CrewRepository;
import com.umc.yourun.repository.UserCrewRepository;
import com.umc.yourun.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrewCommandServiceImpl implements CrewCommandService {
	private final CrewRepository crewRepository;

	private final UserRepository userRepository;

	private final UserCrewRepository userCrewRepository;

	@Override
	@Transactional
	public Crew register(CrewRequestDTO.RegisterDTO request) {
		Crew newCrew=CrewConverter.toCrew(request);
		User admin=userRepository.findById(request.adminId()).orElseThrow();//TODO:예외처리 필요
		UserCrew userCrew= UserCrewConverter.toUserCrew(admin,newCrew);
		newCrew.addUserCrew(userCrew);
		userCrewRepository.save(userCrew);
		return crewRepository.save(newCrew);
	}
}
