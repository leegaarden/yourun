package com.umc.yourun.repository;

import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.Tendency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
