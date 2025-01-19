package com.umc.yourun.repository;

import com.umc.yourun.domain.enums.ChallengeStatus;
import com.umc.yourun.domain.mapping.UserSoloChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umc.yourun.domain.mapping.UserCrewChallenge;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCrewChallengeRepository extends JpaRepository<UserCrewChallenge, Long> {

    // 상태별 크루 찾기
    boolean existsByUserIdAndCrewChallenge_ChallengeStatusIn(
            Long userId,
            List<ChallengeStatus> statuses
    );

    // 챌린지 인원 세기
    long countByCrewChallengeId(Long challengeId);

    // 특정 챌린지의 참여 중인 챌린지 조회
    List<UserCrewChallenge> findParticipantsByCrewChallengeId(Long challengeId);

    // 크루 챌린지 찾기
    Optional<UserCrewChallenge> findByCrewChallengeId(Long challengeId);

    // 솔로 챌린지 삭제
    void deleteAllByCrewChallengeId(Long challengeId);

    // 사용자 아아디로 찾은 userCrewChallenge
    UserCrewChallenge findByUserId (Long userId);

}
