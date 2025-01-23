package com.umc.yourun.domain;

import com.umc.yourun.domain.enums.Tendency;
import com.umc.yourun.domain.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email(message = "올바른 형식의 이메일 주소여야 합니다.")
    @Column(length = 30, nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,10}$", message = "비밀번호는 최소 8자 이상 10자 미만이고 영문, 숫자를 포함해야 합니다.")
    @Column(length = 100, nullable = false)
    private String password;

    
    @Pattern(regexp = "^[가-힣]{2,4}$", message = "닉네임은 띄어쓰기 없이 한클 2~4자만 가능합니다.")
    @Column(length = 10, nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    private Tendency tendency;

    private Long crewReward;

    private Long personalReward;

    private Long mvp;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10) DEFAULT 'ACTIVE'")
    private UserStatus status;

    private LocalDateTime inactive_date;

    public void encodePassword(String password) {
        this.password = password;
    }
}
