package com.umc.yourun.repository;

import com.umc.yourun.domain.User;
import com.umc.yourun.domain.UserMate;
import com.umc.yourun.domain.enums.Tag;
import com.umc.yourun.domain.enums.Tendency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findById(Long id);

    public Optional<User> findByEmail(String email);

    public Optional<User> findByNickname(String name);

    // 사용자 아이디로 닉네임 찾기
    @Query("SELECT u.nickname FROM User u WHERE u.id = :userId")
    Optional<String> findNicknameById(@Param("userId") Long userId);

    // 사용자 아이디로 성향 찾기
    @Query("SELECT u.tendency FROM User u WHERE u.id = :userId")
    Optional<Tendency> findTendencyById(@Param("userId") Long userId);

    @Query(value = "SELECT DISTINCT u.* FROM user u, user_tag ut WHERE u.id = ut.user_id AND u.id not in (:excludeIdList) AND ut.tag in (:userTags) ORDER BY RAND() LIMIT 5", nativeQuery = true)
    List<User> findRandomFive(@Param("excludeIdList") List<Long> excludeIdList, @Param("userTags") List<String> userTags);
}
