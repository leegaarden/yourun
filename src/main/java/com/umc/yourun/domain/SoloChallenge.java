package com.umc.yourun.domain;

import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.custom.ChallengeException;
import com.umc.yourun.domain.BaseEntity;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.ChallengeDistance;
import com.umc.yourun.domain.enums.ChallengePeriod;
import com.umc.yourun.domain.enums.ChallengeStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SoloChallenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeDistance challengeDistance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengePeriod challengePeriod;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus challengeStatus;

    @Builder
    public SoloChallenge(LocalDate endDate, ChallengeDistance challengeDistance) {

        this.startDate = LocalDate.now().plusDays(1); // 시작일은 내일로 고정
        this.endDate = endDate;
        validateDates();
        this.challengeDistance = challengeDistance;
        this.challengeStatus = ChallengeStatus.PENDING; // 처음 생성시 대기로 고정
    }

    // 기간 검사
    private void validateDates() {

        // 시작일이 내일이 아닐 경우
        if (!startDate.equals(LocalDate.now().plusDays(1))) {
            throw new ChallengeException(ErrorCode.INVALID_START_DATE);
        }
        // 마감일이 시작일 이전일 경우
        if (endDate.isBefore(startDate) || endDate.equals(startDate)) {
            throw new ChallengeException(ErrorCode.INVALID_END_DATE);
        }

        // 기간 구하기
        long period = ChronoUnit.DAYS.between(startDate, endDate);
        if (period < 3 || period > 5) {
            throw new ChallengeException(ErrorCode.INVALID_CHALLENGE_PERIOD);
        }

        // 기간 설정
        this.challengePeriod = ChallengePeriod.from(period);

    }

    // 상태 변경
    public void updateStatus(ChallengeStatus status) {
        this.challengeStatus = status;
    }

    // 생성 후 24시간 이내인지 확인
    public boolean isMatchable() {
        return this.getCreatedAt().plusDays(1).isAfter(LocalDateTime.now());
    }

    // 매칭 대기 상태에서 24시간이 지났는지 확인
    public boolean isExpired() {
        return challengeStatus == ChallengeStatus.PENDING &&
                !isMatchable();
    }

    // 챌린지가 종료되었는지 확인
    public boolean isCompleted() {
        return LocalDate.now().isAfter(endDate);
    }

    // Builder의 파라미터로 받은 challengeDistance가 null이 아닌지 확인
    private void validateDistance(ChallengeDistance challengeDistance) {
        if (challengeDistance == null) {
            throw new ChallengeException(ErrorCode.INVALID_CHALLENGE_DISTANCE);
        }
    }
}
