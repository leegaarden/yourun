package com.umc.yourun.converter;

import com.umc.yourun.domain.User;
import com.umc.yourun.domain.UserTag;
import com.umc.yourun.domain.enums.Tag;
import com.umc.yourun.domain.enums.UserStatus;
import com.umc.yourun.dto.user.UserRequestDTO;
import com.umc.yourun.dto.user.UserResponseDTO;
import com.umc.yourun.repository.RunningDataRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserConverter {
	private RunningDataRepository runningDataRepository;

	@Autowired
	public UserConverter(RunningDataRepository runningDataRepository) {
		this.runningDataRepository = runningDataRepository;
	}

	public static User toMember(UserRequestDTO.JoinDto request) {

		return User.builder()
			.nickname(request.nickname())
			.email(request.email())   // 추가된 코드
			.password(request.password())   // 추가된 코드
			.tendency(request.tendency())
			.personalReward(0L)
			.crewReward(0L)
			.mvp(0L)
			.status(UserStatus.valueOf("ACTIVE"))
			.build();
	}

	public static UserResponseDTO.userMateInfo toUserMateInfo(User mate, int distance, int countDay) {
		List<Tag> tags = getTagsByUser(mate);

		return UserResponseDTO.userMateInfo.builder()
			.id(mate.getId())
			.nickname(mate.getNickname())
			.tendency(mate.getTendency())
			.tags(tags)
			.totalDistance(distance)
			.countDay(countDay)
			.build();
	}

	public static UserResponseDTO.userInfo toUserInfo(User user) {
		List<Tag> tags = getTagsByUser(user);

		return UserResponseDTO.userInfo.builder()
			.id(user.getId())
			.nickname(user.getNickname())
			.tendency(user.getTendency())
			.tags(tags)
			.crewReward(user.getCrewReward())
			.personalReward(user.getPersonalReward())
			.mvp(user.getMvp())
			.build();
	}
	private static List<Tag> getTagsByUser(User user) {
		List<Tag> tags = new ArrayList<>();
		for (UserTag userTag : user.getUserTags()) {
			tags.add(userTag.getTag());
		}
		return tags;
	}
}
