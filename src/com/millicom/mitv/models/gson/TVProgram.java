package com.millicom.mitv.models.gson;

import java.util.ArrayList;
import java.util.Date;

public class TVProgram {
	
	String category;
	String programType;
	String programId;
	String title;
	String synopsisShort;
	String synopsisLong;
	ImageSetOrientation images;
	ArrayList<String> tags;
	ArrayList<TVCredit> credits;
	Long beginTimeMillis;
	Date beginTime;
	Date endTime;
	String broadcastType;
	String shareUrl;
	
	public String getCategory() {
		return category;
	}
	public String getProgramType() {
		return programType;
	}
	public String getProgramId() {
		return programId;
	}
	public String getTitle() {
		return title;
	}
	public String getSynopsisShort() {
		return synopsisShort;
	}
	public String getSynopsisLong() {
		return synopsisLong;
	}
	public ImageSetOrientation getImages() {
		return images;
	}
	public ArrayList<String> getTags() {
		return tags;
	}
	public ArrayList<TVCredit> getCredits() {
		return credits;
	}
	public Long getBeginTimeMillis() {
		return beginTimeMillis;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public String getBroadcastType() {
		return broadcastType;
	}
	public String getShareUrl() {
		return shareUrl;
	}
	
	

	
}
