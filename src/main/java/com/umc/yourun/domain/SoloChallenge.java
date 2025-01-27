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
import lombok.*;

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
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeDistance challengeDistance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengePeriod challengePeriod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeStatus challengeStatus;

    @OneToMany(mappedBy = "soloChallenge")
    private List<UserSoloChallenge> userSoloChallenges = new ArrayList<>();

    @Builder
    public SoloChallenge(LocalDateTime startDate, LocalDateTime endDate, ChallengeDistance challengeDistance, ChallengePeriod challengePeriod) {
        this.startDate = startDate;
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
    // true 반환: 아직 매칭 가능한 상태 (24시간 이내)
    // false 반환: 매칭 불가능한 상태 (24시간 초과)
    public boolean isMatchable() {
        return this.getCreatedAt().plusDays(1).isAfter(LocalDateTime.now());
    }
}
