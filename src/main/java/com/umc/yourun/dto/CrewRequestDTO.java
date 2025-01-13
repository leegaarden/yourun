package com.umc.yourun.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrewRequestDTO() {
	public record RegisterDTO(
		Long adminId,
		@NotNull(message = "크루 이름은 필수 값입니다.")
		@Size(min = 1,max = 50,message = "이름은 1자 이상 50자 이하여야 합니다.")
		String name,
		@Size(max = 50,message = "응원 문구는 50자 이하여야 합니다.")
		String cheerMessage//응원 문구
	)
	{}
}
