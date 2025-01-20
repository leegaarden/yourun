package com.umc.yourun.domain.mapping;

import com.umc.yourun.domain.BaseEntity;
import com.umc.yourun.domain.SoloChallenge;
import com.umc.yourun.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSoloChallenge extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solo_challenge_id", nullable = false)
    private SoloChallenge soloChallenge;

    @Builder
    public UserSoloChallenge(User user, SoloChallenge soloChallenge) {
        this.user = user;
        this.soloChallenge = soloChallenge;
    }

}