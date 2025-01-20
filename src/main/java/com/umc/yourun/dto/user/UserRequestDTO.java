package com.umc.yourun.dto.user;

import com.umc.yourun.domain.enums.Tag;

public class UserRequestDTO {
    public record JoinDto(
        String email,
        String password,
        String passwordcheck,
        String nickname,
        Tag tag1,
        Tag tag2
    ){}

    public record LoginDto(
            String email,
            String password
    ){}
}
