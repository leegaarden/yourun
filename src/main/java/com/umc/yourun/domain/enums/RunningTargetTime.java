package com.umc.yourun.domain.enums;

public enum RunningTargetTime {
	FIFTEEN_MINUTES(15),
	THIRTY_MINUTES(30),
	FORTY_FIVE_MINUTES(45),
	SIXTY_MINUTES(60);

	private final int time;

	RunningTargetTime(int time) {
		this.time = time;
	}

	public int getTime() {
		return time;
	}
}
