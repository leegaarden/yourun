package com.umc.yourun.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.umc.yourun.domain.enums.RunningDataStatus;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Slf4j
public class RunningData extends BaseEntity{
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

	@Column
	@Enumerated(EnumType.STRING)
	private RunningDataStatus status;

	//단위 : 1km당 걸리는 's'
	@Column
	private Integer pace;

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
		this.status = RunningDataStatus.ACTIVE;
		this.pace = calculatePace();
		log.info("pace: {}",pace);
	}

	private Integer calculatePace() {
		if(totalTime==0) {
			return 0;
		}
		BigDecimal pace = new BigDecimal(totalTime).divide(new BigDecimal(totalDistance/1000), 2, RoundingMode.HALF_UP);
		log.info("distance to km: {}",totalDistance/1000);
		return pace.intValue();
	}


}
