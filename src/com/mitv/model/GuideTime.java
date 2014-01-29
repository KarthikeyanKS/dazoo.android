package com.mitv.model;


public class GuideTime implements Comparable<GuideTime> {

	private int	timeOfDay;

	public GuideTime() {
	}

	public void setTimeOfDay(int timeOfDay) {
		this.timeOfDay = timeOfDay;
	}

	public int getTimeOfDay() {
		return this.timeOfDay;
	}

	@Override
	public int compareTo(GuideTime guideTime) {
		int otherTimeOfDay = guideTime.getTimeOfDay();
		if (this.timeOfDay == otherTimeOfDay) {
			return 1;
		} else return 0;
	}

}
