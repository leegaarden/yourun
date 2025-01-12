package com.umc.yourun.domain;

import com.umc.yourun.domain.challenge.CrewChallenge;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Crew extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String name;

    private Integer win;

    @Column(length = 10)
    private String admin;

    @OneToMany(mappedBy = "crew")
    private List<CrewChallenge> crewChallenges = new ArrayList<>();

    @Builder
    public Crew(String name, String admin) {
        this.name = name;
        this.admin = admin;
        this.win = 0;
    }
}
