package com.umc.yourun.domain;

<<<<<<< HEAD
import com.umc.yourun.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import javax.swing.text.html.HTML;
import java.time.LocalDateTime;
=======
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
>>>>>>> 574e53559b07f8a2e520d67d210bbc854e921906

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
    @Column(length = 20, nullable = false, unique = true)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 10, nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10) DEFAULT 'ACTIVE'")
    private UserStatus status;

    private LocalDateTime inactive_date;

    public void encodePassword(String password){
        this.password = password;
=======
    @Builder
    public User (Long id) {
        this.id = id;
>>>>>>> 574e53559b07f8a2e520d67d210bbc854e921906
    }
}
