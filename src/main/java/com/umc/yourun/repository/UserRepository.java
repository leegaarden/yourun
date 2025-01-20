package com.umc.yourun.repository;

<<<<<<< HEAD
import com.umc.yourun.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findById(Long id);

    public Optional<User> findByEmail(String email);
=======
import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.yourun.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
>>>>>>> 574e53559b07f8a2e520d67d210bbc854e921906
}
