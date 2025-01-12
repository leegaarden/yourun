package com.umc.yourun.domain.challenge;

import com.umc.yourun.domain.BaseEntity;
import com.umc.yourun.domain.Crew;
import com.umc.yourun.domain.enums.ChallengeStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewChallenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus challengeStatus;

    @Builder
    public CrewChallenge(Crew crew, Challenge challenge) {
        this.crew = crew;
        this.challenge = challenge;
        this.challengeStatus = ChallengeStatus.START;
    }
}
