package com.umc.yourun.domain.challenge;

import com.umc.yourun.domain.BaseEntity;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.ChallengeStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SoloChallenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus challengeStatus;

    @Builder
    public SoloChallenge(Challenge challenge, User user) {
        this.challenge = challenge;
        this.user = user;
        this.challengeStatus = ChallengeStatus.START;
    }
}
