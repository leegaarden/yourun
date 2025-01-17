package com.umc.yourun.domain.mapping;

import com.umc.yourun.domain.BaseEntity;
import com.umc.yourun.domain.CrewChallenge;
import com.umc.yourun.domain.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCrewChallenge extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "crew_challenge_id")
	private CrewChallenge crewChallenge;

	@Builder
	public UserCrewChallenge (User user, CrewChallenge crewChallenge) {
		this.user = user;
		this.crewChallenge = crewChallenge;
	}
}
