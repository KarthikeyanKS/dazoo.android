package com.mitv.model;


public class OldGuideTime implements Comparable<OldGuideTime> {

	private int	timeOfDay;

	public OldGuideTime() {
	}

	public void setTimeOfDay(int timeOfDay) {
		this.timeOfDay = timeOfDay;
	}

	public int getTimeOfDay() {
		return this.timeOfDay;
	}

	@Override
	public int compareTo(OldGuideTime guideTime) {
		int otherTimeOfDay = guideTime.getTimeOfDay();
		if (this.timeOfDay == otherTimeOfDay) {
			return 1;
		} else return 0;
	}

}
