package com.umc.yourun.converter;

import com.umc.yourun.domain.Crew;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.CrewRole;
import com.umc.yourun.domain.enums.UserCrewStatus;
import com.umc.yourun.domain.mapping.UserCrew;

public class UserCrewConverter {
	public static UserCrew toUserCrew(User user, Crew crew) {
		return UserCrew.builder()
			.role(CrewRole.ADMIN)
			.status(UserCrewStatus.APPROVED)
			.crew(crew)
			.user(user)
			.build();
	}
}
