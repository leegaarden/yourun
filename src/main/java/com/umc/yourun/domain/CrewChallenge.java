package com.umc.yourun.domain;

import com.umc.yourun.domain.BaseEntity;
import com.umc.yourun.domain.Crew;
import com.umc.yourun.domain.User;
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
public class CrewChallenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Crew-Challenge가 생성될 때 자동으로 생성되는 Crew와의 일대일 관계
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus challengeStatus;

    @Builder
    public CrewChallenge(Crew crew, User user, LocalDate startDate,
                         LocalDate endDate) {
        this.crew = crew;
        this.user = user;
        this.startDate = startDate; // TODO: 내일 날짜 고정으로 수정
        this.endDate = endDate;
        this.challengeStatus = ChallengeStatus.WAIT; // 처음에는 대기 상태로 생성
    }
}
