package com.umc.yourun.domain;

import com.umc.yourun.domain.enums.UserStatus;
import com.umc.yourun.domain.mapping.UserSoloChallenge;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 10, nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10) DEFAULT 'ACTIVE'")
    private UserStatus status;

    private LocalDateTime inactive_date;

    // FIXME: 챌린지 조회 중 러닝데이터가 필요해서  임의로 넣었습니다. 이후에 수정해주세요.
    @OneToMany(mappedBy = "user")
    private List<RunningData> runningData = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Getter
    private List<UserTag> userTags = new ArrayList<>();

    public void encodePassword(String password) {
        this.password = password;
    }
}
