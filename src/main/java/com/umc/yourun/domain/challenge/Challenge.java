package com.umc.yourun.domain.challenge;

import com.umc.yourun.domain.BaseEntity;
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

    @Column(length = 30, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private ChallengeKind kind;

    @Enumerated(EnumType.STRING)
    private ChallengeDistance distance;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @OneToMany(mappedBy = "challenge")
    private List<SoloChallenge> soloChallenges = new ArrayList<>();

    @OneToMany(mappedBy = "challenge")
    private List<CrewChallenge> crewChallenges = new ArrayList<>();

    @Builder
    public Challenge(String name, ChallengeKind kind, ChallengeDistance distance,
                     LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.kind = kind;
        this.distance = distance;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}