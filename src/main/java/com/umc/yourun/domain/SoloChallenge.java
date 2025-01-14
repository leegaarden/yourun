package com.umc.yourun.domain;

import com.umc.yourun.domain.BaseEntity;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.ChallengeDistance;
import com.umc.yourun.domain.enums.ChallengeStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SoloChallenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ChallengeDistance challengeDistance;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus challengeStatus;

    @Builder
    public SoloChallenge(User user, LocalDate startDate, LocalDate endDate,
                         ChallengeDistance challengeDistance, ChallengeStatus challengeStatus) {

        this.user = user;
        this.startDate = startDate; // TODO: 내일 날짜 고정으로 수정
        this.endDate = endDate;
        this.challengeDistance = challengeDistance;
        this.challengeStatus = ChallengeStatus.PENDING; // 처음에는 대기 상태로 생성

    }
}
