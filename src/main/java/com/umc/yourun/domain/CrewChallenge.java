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
    private String slogan;    // 구호

    @Setter
    @Column
    private Long matchedCrewChallengeId;  // 매칭된 크루 챌린지 ID

    @OneToMany(mappedBy = "crewChallenge")
    private List<UserCrewChallenge> userCrews = new ArrayList<>();

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