package com.umc.yourun.domain;

import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.GeneralException;
import com.umc.yourun.config.exception.custom.RankingException;
import com.umc.yourun.domain.enums.RankingType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "rankings",
        indexes = {
                @Index(name = "idx_score", columnList = "score DESC")
        }
)
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Min(0)
    private int score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RankingType rankingType;                //pace와 distance

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @Column(nullable = true)
    private int sortOrder;

    @Builder
    public Ranking(User user, int score, RankingType rankingType) {
        if (user == null) {
            throw new GeneralException(ErrorCode.USER_NOT_FOUND);
        }
        if (score < 0) {
            throw new RankingException(ErrorCode.INVALID_RANKING_SCORE);
        }
        if (rankingType == null) {
            throw new RankingException(ErrorCode.INVALID_RANKING_SCORE);
        }

        this.user = user;
        this.score = score;
        this.rankingType = rankingType;
        this.lastUpdated = LocalDateTime.now();
        this.sortOrder = 0;     //0이면 아직 정렬되지 않은 것
    }

    public void updateScore(int score) {

        if (score < 0) {
            throw new RankingException(ErrorCode.INVALID_RANKING_SCORE);
        }

        this.score = score;
        this.lastUpdated = LocalDateTime.now();
    }

    public void updateSortOrder(int sortOrder) {

        if (sortOrder <= 0) {
            throw new RankingException(ErrorCode.INVALID_RANKING_SORTORDER);
        }

        this.sortOrder = sortOrder;
    }
}
