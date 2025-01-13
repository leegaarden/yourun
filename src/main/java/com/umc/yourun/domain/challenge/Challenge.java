package com.umc.yourun.domain.challenge;

import com.umc.yourun.domain.BaseEntity;
import com.umc.yourun.domain.Crew;
import com.umc.yourun.domain.enums.ChallengeDistance;
import com.umc.yourun.domain.enums.ChallengeKind;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Challenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ChallengeKind kind;

    @Enumerated(EnumType.STRING)
    private ChallengeDistance distance;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    // Challenge가 생성될 때 자동으로 생성되는 Crew와의 일대일 관계
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @OneToMany(mappedBy = "challenge")
    private List<SoloChallenge> soloChallenges = new ArrayList<>();

    @OneToMany(mappedBy = "challenge")
    private List<CrewChallenge> crewChallenges = new ArrayList<>();

    @Builder
    public Challenge(ChallengeKind kind, ChallengeDistance distance,
                     LocalDate startDate, LocalDate endDate, String crewName) {
        this.kind = kind;
        this.distance = distance;
        this.startDate = startDate;
        this.endDate = endDate;
        // 챌린지 생성될 때 크루 자동 생성
        this.crew = Crew.builder()
                .name(crewName)
                .admin("SYSTEM")
                .build();
    }
}