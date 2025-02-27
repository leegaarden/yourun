package com.umc.yourun.dto.user;

import com.umc.yourun.domain.enums.Tag;
import com.umc.yourun.domain.enums.Tendency;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.List;

public class UserResponseDTO {

	@Builder
	public record userMateInfo(
		@Schema(example = "1")
		Long id,
		@Schema(example = "최석운")
		String nickname,
		@Schema(example = "페이스메이커")
		Tendency tendency,
		List<Tag> tags,
		int totalDistance,
		int countDay
	) {
	}

	@Builder
	public record userInfo(
		@Schema(example = "1")
		Long id,
		@Schema(example = "최석운")
		String nickname,
		@Schema(example = "페이스메이커")
		Tendency tendency,
		@Schema(example = "#느긋하게,#에너자이저")
		List<Tag> tags,
		@Schema(example = "1")
		Long crewReward,
		@Schema(example = "1")
		Long personalReward,
		@Schema(example = "1")
		Long mvp
		) {
	}
}
