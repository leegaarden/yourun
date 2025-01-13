package com.umc.yourun.domain.mapping;

import com.umc.yourun.domain.BaseEntity;
import com.umc.yourun.domain.Crew;
import com.umc.yourun.domain.User;
import com.umc.yourun.domain.enums.CrewRole;
import com.umc.yourun.domain.enums.UserCrewStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class UserCrew extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userCrewId;
	@Column
	@Enumerated(EnumType.STRING)
	private UserCrewStatus status;
	@Column
	@Enumerated(EnumType.STRING)
	private CrewRole role;//추가된 사항, 논의 필요

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "crew_id")
	private Crew crew;
}
