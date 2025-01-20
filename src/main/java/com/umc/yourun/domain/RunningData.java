package com.umc.yourun.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.umc.yourun.domain.mapping.UserCrew;

import ch.qos.logback.core.Layout;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RunningData {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private Integer targetTime;

	@Column
	private LocalDateTime startTime;

	@Column
	private LocalDateTime endTime;

	//단위 : m
	@Column
	private Integer totalDistance;

	//단위 : s
	@Column
	private Integer totalTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Builder
	public RunningData(Integer targetTime,LocalDateTime startTime, LocalDateTime endTime, Integer totalDistance, Integer totalTime, User user) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.targetTime = targetTime;
		this.totalDistance = totalDistance;
		this.totalTime = totalTime;
		this.user = user;
	}

}
