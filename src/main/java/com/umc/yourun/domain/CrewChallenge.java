package com.umc.yourun.domain;

import com.umc.yourun.domain.enums.ChallengePeriod;
import com.umc.yourun.domain.enums.ChallengeStatus;
import com.umc.yourun.domain.mapping.UserCrewChallenge;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewChallenge extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus challengeStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengePeriod challengePeriod;

    @Column(nullable = false)
    private String crewName;    // 기존 crew의 name 속성

    @Column(nullable = false)
    private int winningCount;    // 기존 crew의 winning_count 속성

    @OneToMany(mappedBy = "crewChallenge")
    private List<UserCrewChallenge> userCrews = new ArrayList<>();


    @Builder
    public CrewChallenge(String crewName, LocalDate endDate, ChallengePeriod challengePeriod) {
        this.crewName = crewName;
        this.winningCount = 0;   // 처음 생성시 0으로 초기화
        this.startDate = LocalDate.now().plusDays(1);
        this.endDate = endDate;
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