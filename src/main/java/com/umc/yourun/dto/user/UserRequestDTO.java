package com.umc.yourun.dto.user;

import com.umc.yourun.domain.enums.Tag;
import com.umc.yourun.domain.enums.Tendency;
import io.swagger.v3.oas.annotations.media.Schema;

public class UserRequestDTO {
    public record JoinDto(
        @Schema(example = "gkak921@naver.com")
        String email,
        @Schema(example = "gkak1234")
        String password,
        @Schema(example = "gkak1234")
        String passwordcheck,
        @Schema(example = "최석운")
        String nickname,
        @Schema(example = "페이스메이커")
        Tendency tendency,
        @Schema(example = "느긋하게")
        Tag tag1,
        @Schema(example = "음악과")
        Tag tag2
    ){}

    public record LoginDto(
            @Schema(example = "gkak921@naver.com")
            String email,
            @Schema(example = "gkak1234")
            String password
    ){}
}
