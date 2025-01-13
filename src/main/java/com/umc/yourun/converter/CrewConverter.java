package com.umc.yourun.converter;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.umc.yourun.domain.Crew;
import com.umc.yourun.dto.CrewRequestDTO;
import com.umc.yourun.dto.CrewResponseDTO;

public class CrewConverter {
	public static CrewResponseDTO.RegisterResultDTO toRegisterResultDTO(Crew crew){
		return CrewResponseDTO.RegisterResultDTO.builder()
			.crewId(crew.getId())
			.createdAt(LocalDateTime.now())
			.build();
	}

	public static Crew toCrew(CrewRequestDTO.RegisterDTO request){

		return Crew.builder()
			.name(request.name())
			.cheerMessage(request.cheerMessage())
			.adminId(request.adminId())
			.winningCount(0)
			.userCrews(new ArrayList<>())
			.build();
	}
}
