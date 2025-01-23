package com.umc.yourun.dto.user;

import com.umc.yourun.domain.enums.Tag;
import com.umc.yourun.domain.enums.Tendency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UserRequestDTO {
    public record JoinDto(
        @NotBlank
        @Email(message = "올바른 형식의 이메일 주소여야 합니다.")
        @Schema(example = "gkak921@naver.com")
        String email,
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,10}$", message = "비밀번호는 영문과 숫자를 포함하여 8~10자이어야 합니다.")
        @Schema(example = "gkak1234")
        String password,
        @Schema(example = "gkak1234")
        String passwordcheck,
        @Pattern(regexp = "^[가-힣]{2,4}$", message = "닉네임은 띄어쓰기 없이 한글 2~4자만 가능합니다.")
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
