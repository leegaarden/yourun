package com.umc.yourun.domain;

import com.umc.yourun.config.exception.ErrorCode;
import com.umc.yourun.config.exception.custom.ChallengeException;
import com.umc.yourun.domain.BaseEntity;
import com.umc.yourun.domain.Crew;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.ChallengePeriod;
import com.umc.yourun.domain.enums.ChallengeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewChallenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Crew-Challenge가 생성될 때 자동으로 생성되는 Crew와의 일대일 관계
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengePeriod challengePeriod;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus challengeStatus;

    @Builder
    public CrewChallenge(String crewName, LocalDate endDate) {
        // 크루명 검사 먼저 한 뒤에 생성
        validateCrewName(crewName);
        this.crew = Crew.builder()
                .name(crewName)
                .winningCount(0)
                .build();
        this.startDate = LocalDate.now().plusDays(1);
        this.endDate = endDate;
        validateDates();
        this.challengeStatus = ChallengeStatus.PENDING;
    }

    // 크루명 조건 검사
    private void validateCrewName(String crewName) {
        if (crewName == null || crewName.trim().isEmpty()) { // 비어있는지 검사
            throw new ChallengeException(ErrorCode.INVALID_CREW_NAME_NULL);
        }
        if (crewName.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) { // 특수 문자 검사
            throw new ChallengeException(ErrorCode.INVALID_CREW_NAME_FORMAT1);
        }
        if (crewName.length() < 3 || crewName.length() > 5) { // 글자 수 검사
            throw new ChallengeException(ErrorCode.INVALID_CREW_NAME_FORMAT2);
        }
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

}
