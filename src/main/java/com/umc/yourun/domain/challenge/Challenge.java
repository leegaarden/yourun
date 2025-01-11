package com.umc.yourun.domain.challenge;

import com.umc.yourun.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
public class Challenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
}
