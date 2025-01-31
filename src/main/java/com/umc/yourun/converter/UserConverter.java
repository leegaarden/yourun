package com.umc.yourun.converter;

import com.umc.yourun.domain.User;
import com.umc.yourun.domain.UserTag;
import com.umc.yourun.domain.enums.Tag;
import com.umc.yourun.domain.enums.UserStatus;
import com.umc.yourun.dto.user.UserRequestDTO;
import com.umc.yourun.dto.user.UserResponseDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserConverter {
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

    public static UserResponseDTO.userMateInfo toUserMateInfo(User mate) {
        List<Tag> tags = new ArrayList<>();
        for(UserTag userTag: mate.getUserTags()){
            tags.add(userTag.getTag());
        }

        return UserResponseDTO.userMateInfo.builder()
                .id(mate.getId())
                .nickname(mate.getNickname())
                .tendency(mate.getTendency())
                .tags(tags)
                .build();
    }
}
