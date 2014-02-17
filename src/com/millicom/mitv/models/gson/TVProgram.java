package com.millicom.mitv.models.gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class TVProgram implements JsonDeserializer<TVProgram> {
	
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
	
	public TVProgram() {
		
	}

	@Override
	public TVProgram deserialize(JsonElement arg0, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getProgramType() {
		return programType;
	}

	public void setProgramType(String programType) {
		this.programType = programType;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSynopsisShort() {
		return synopsisShort;
	}

	public void setSynopsisShort(String synopsisShort) {
		this.synopsisShort = synopsisShort;
	}

	public String getSynopsisLong() {
		return synopsisLong;
	}

	public void setSynopsisLong(String synopsisLong) {
		this.synopsisLong = synopsisLong;
	}

	public ImageSetOrientation getImages() {
		return images;
	}

	public void setImages(ImageSetOrientation images) {
		this.images = images;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}

	public ArrayList<TVCredit> getCredits() {
		return credits;
	}

	public void setCredits(ArrayList<TVCredit> credits) {
		this.credits = credits;
	}

	public Long getBeginTimeMillis() {
		return beginTimeMillis;
	}

	public void setBeginTimeMillis(Long beginTimeMillis) {
		this.beginTimeMillis = beginTimeMillis;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getBroadcastType() {
		return broadcastType;
	}

	public void setBroadcastType(String broadcastType) {
		this.broadcastType = broadcastType;
	}

	public String getShareUrl() {
		return shareUrl;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}
	
	
}
