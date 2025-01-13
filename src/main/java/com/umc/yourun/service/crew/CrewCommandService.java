package com.umc.yourun.service.crew;

import com.umc.yourun.domain.Crew;
import com.umc.yourun.dto.CrewRequestDTO;

import jakarta.validation.Valid;

public interface CrewCommandService {

	Crew register(@Valid CrewRequestDTO.RegisterDTO request);
}
