package com.umc.yourun.converter;

import com.umc.yourun.domain.User;
import com.umc.yourun.domain.UserTag;
import com.umc.yourun.domain.enums.Tag;
import org.springframework.stereotype.Component;

@Component
public class UserTagConverter {
    static public UserTag toUserTag(User user, Tag tag){
        return UserTag.builder().user(user).tag(tag).build();
    }
}
