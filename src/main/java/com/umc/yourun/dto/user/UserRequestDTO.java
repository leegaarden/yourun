package com.umc.yourun.dto.user;

import com.umc.yourun.domain.enums.Tag;
import com.umc.yourun.domain.enums.Tendency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class UserRequestDTO {
    public record JoinDto(
        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "올바른 형식의 이메일 주소여야 합니다.")
        @Schema(example = "gkak921@naver.com")
        String email,
        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,10}$", message = "비밀번호는 영문과 숫자를 포함하여 8~10자이어야 합니다.")
        @Schema(example = "gkak1234")
        String password,
        @NotBlank(message = "비밀번호 확인을 위해 입력해 주세요.")
        @Schema(example = "gkak1234")
        String passwordcheck,
        @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
        @Pattern(regexp = "^[가-힣]{2,4}$", message = "닉네임은 띄어쓰기 없이 한글 2~4자만 가능합니다.")
        @Schema(example = "최석운")
        String nickname,
        @NotNull(message = "성향은 필수 입력 항목입니다.")
        @Schema(example = "페이스메이커")
        Tendency tendency,
        @NotNull(message = "태그는 2개를 필수로 선택해야 하는 항목입니다.")
        @Schema(example = "느긋하게")
        Tag tag1,
        @NotNull(message = "태그는 2개를 필수로 선택해야 하는 항목입니다.")
        @Schema(example = "음악과")
        Tag tag2
    ){}

    public record LoginDto(
            @NotBlank(message = "이메일을 입력해 주세요.")
            @Schema(example = "gkak921@naver.com")
            String email,
            @NotBlank(message = "비밀번호를 입력해 주세요.")
            @Schema(example = "gkak1234")
            String password
    ){}

    public record SetKakaoUserDto(
            @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
            @Pattern(regexp = "^[가-힣]{2,4}$", message = "닉네임은 띄어쓰기 없이 한글 2~4자만 가능합니다.")
            @Schema(example = "최석운")
            String nickname,
            @NotNull(message = "성향은 필수 입력 항목입니다.")
            @Schema(example = "페이스메이커")
            Tendency tendency,
            @NotNull(message = "태그는 2개를 필수로 선택해야 하는 항목입니다.")
            @Schema(example = "느긋하게")
            Tag tag1,
            @NotNull(message = "태그는 2개를 필수로 선택해야 하는 항목입니다.")
            @Schema(example = "음악과")
            Tag tag2
    ){}

    public record UpdateDto(
        @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
        @Pattern(regexp = "^[가-힣]{2,4}$", message = "닉네임은 띄어쓰기 없이 한글 2~4자만 가능합니다.")
        @Schema(example = "최석운")
        String nickname,
        @NotNull(message = "태그는 2개를 필수로 선택해야 하는 항목입니다.")
        @Schema(example = "느긋하게")
        Tag tag1,
        @NotNull(message = "태그는 2개를 필수로 선택해야 하는 항목입니다.")
        @Schema(example = "음악과")
        Tag tag2
    ){}
}
