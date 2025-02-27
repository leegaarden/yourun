package com.umc.yourun.domain.enums;

public enum RunningTargetTime {
	FIFTEEN_MINUTES(15),
	THIRTY_MINUTES(30),
	FORTY_FIVE_MINUTES(45),
	SIXTY_MINUTES(60);

	private final int time;

	RunningTargetTime(int time) {
		if(time%15 != 0) {
			throw new IllegalArgumentException("시간은 15의 배수여야 합니다.");
		}
		this.time = time;
	}

	public int getTime() {
		return time;
	}
}
