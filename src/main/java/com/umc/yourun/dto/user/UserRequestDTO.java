package com.umc.yourun.dto.user;

import com.umc.yourun.domain.enums.Tag;
import com.umc.yourun.domain.enums.Tendency;

public class UserRequestDTO {
    public record JoinDto(
        String email,
        String password,
        String passwordcheck,
        String nickname,
        Tendency tendency,
        Tag tag1,
        Tag tag2
    ){}

    public record LoginDto(
            String email,
            String password
    ){}
}
