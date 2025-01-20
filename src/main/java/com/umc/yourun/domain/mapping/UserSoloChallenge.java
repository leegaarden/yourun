package com.umc.yourun.domain.mapping;

import com.umc.yourun.domain.BaseEntity;
import com.umc.yourun.domain.SoloChallenge;
import com.umc.yourun.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSoloChallenge extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solo_challenge_id", nullable = false)
    private SoloChallenge soloChallenge;

    @Getter
    @Column(nullable = false)
    private boolean isCreator;

}