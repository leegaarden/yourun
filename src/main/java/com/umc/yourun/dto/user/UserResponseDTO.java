package com.umc.yourun.dto.user;

import com.umc.yourun.domain.enums.Tag;
import com.umc.yourun.domain.enums.Tendency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.List;

public class UserResponseDTO {

    @Builder
    public record userMateInfo(
            Long id,
            @Pattern(regexp = "^[가-힣]{2,4}$", message = "닉네임은 띄어쓰기 없이 한글 2~4자만 가능합니다.")
            @Schema(example = "최석운")
            String nickname,
            @Schema(example = "페이스메이커")
            Tendency tendency,
            List<Tag> tags
    ){}
}
