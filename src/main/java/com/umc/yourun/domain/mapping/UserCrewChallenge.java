package com.umc.yourun.domain.mapping;

import com.umc.yourun.domain.BaseEntity;
import com.umc.yourun.domain.CrewChallenge;
import com.umc.yourun.domain.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCrewChallenge extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "crew_challenge_id")
	private CrewChallenge crewChallenge;

	@Getter
	@Column(nullable = false)
	private boolean isCreator;

}
