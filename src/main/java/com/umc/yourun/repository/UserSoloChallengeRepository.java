package com.umc.yourun.repository;

import com.umc.yourun.domain.enums.ChallengeStatus;
import com.umc.yourun.domain.mapping.UserSoloChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSoloChallengeRepository extends JpaRepository<UserSoloChallenge, Long> {

    // Optional 반환
    Optional<UserSoloChallenge> findByUserIdAndSoloChallenge_ChallengeStatusIn(
            Long userId,
            List<ChallengeStatus> statuses
    );

    // boolean 반환
    boolean existsByUserIdAndSoloChallenge_ChallengeStatusIn(
            Long userId,
            List<ChallengeStatus> statuses
    );

    // 솔로 챌린지 찾기
    Optional<UserSoloChallenge> findBySoloChallengeId(Long challengeId);

    // 솔로 챌린지 삭제
    void deleteAllBySoloChallengeId(Long challengeId);

    // 사용자가 어떤 챌린지에든 참여하고 있는지 확인
    boolean existsByUserId(Long userId);

    // 사용자 아이디로 찾은 UserSoloChallenge
    UserSoloChallenge findByUserId (Long UserId);

    // 사용자의 챌린지 메이트 찾기
    Optional<UserSoloChallenge> findBySoloChallengeIdAndUserIdNot(Long challengeId, Long userId);

}