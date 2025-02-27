package com.umc.yourun.converter;

import com.umc.yourun.domain.User;
import com.umc.yourun.domain.UserMate;

public class UserMateConverter {
    static public UserMate toUserMate(User user, User mate){
        return UserMate.builder().user(user).mate(mate).build();
    }
}
