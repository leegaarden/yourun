package com.umc.yourun.domain;

import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.custom.ChallengeException;
import com.umc.yourun.domain.BaseEntity;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.ChallengeDistance;
import com.umc.yourun.domain.enums.ChallengePeriod;
import com.umc.yourun.domain.enums.ChallengeStatus;
import com.umc.yourun.domain.mapping.UserSoloChallenge;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SoloChallenge extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeDistance challengeDistance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengePeriod challengePeriod;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus challengeStatus;

    @OneToMany(mappedBy = "soloChallenge")
    private List<UserSoloChallenge> userSoloChallenges = new ArrayList<>();

    @Builder
    public SoloChallenge(LocalDate endDate, ChallengeDistance challengeDistance, ChallengePeriod challengePeriod) {
        this.startDate = LocalDate.now().plusDays(1);
        this.endDate = endDate;
        this.challengeDistance = challengeDistance;
        this.challengeStatus = ChallengeStatus.PENDING;
        this.challengePeriod = challengePeriod;
    }

    // 상태 변경
    public void updateStatus(ChallengeStatus status) {
        this.challengeStatus = status;
    }

    // 생성 후 24시간 이내인지 확인
    public boolean isMatchable() {
        return this.getCreatedAt().plusDays(1).isAfter(LocalDateTime.now());
    }
}
