package com.umc.yourun.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private Long age;

    @Column(length = 30, nullable = false)
    private String area;

    private LocalDateTime inactive_date;

    @Column(nullable = false)
    private Long height;

    @Column(nullable = false)
    private Long weight;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10) DEFAULT 'ACTIVE'")
    private Status status;
}
