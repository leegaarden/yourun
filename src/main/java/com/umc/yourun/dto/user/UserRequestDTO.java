package com.umc.yourun.dto.user;

import com.umc.yourun.domain.User;

import java.util.List;

public class UserRequestDTO {
    public record JoinDto(
        String email,
        String password,
        String passwordcheck,
        String nickname
    ){}

    public record LoginDto(
            String email,
            String password
    ){}
}
