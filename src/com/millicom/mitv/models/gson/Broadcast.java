package com.millicom.mitv.models.gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.millicom.mitv.enums.BroadcastTypeEnum;
import com.mitv.Consts;
import com.mitv.utilities.DateUtilities;

public class Broadcast {
	private TVProgram program;
	private Long beginTimeMillis;
	private String beginTime;
	private String endTime;
	private BroadcastTypeEnum broadcastType;
	private String shareUrl;
	
	private transient Date beginTimeObject;
	private transient Date endTimeObject;
	
	public TVProgram getProgram() {
		return program;
	}
	
	public Long getBeginTimeMillis() {
		return beginTimeMillis;
	}
	
	public Date getBeginTime() {
		if(beginTimeObject == null) {
			SimpleDateFormat dfmInput = DateUtilities.getDateFormat(Consts.TVDATE_DATE_FORMAT);
			try {
				beginTimeObject = dfmInput.parse(beginTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return beginTimeObject;
	}
	
	public Date getEndTime() {
		if(endTimeObject == null) {
			SimpleDateFormat dfmInput = DateUtilities.getDateFormat(Consts.TVDATE_DATE_FORMAT);
			try {
				endTimeObject = dfmInput.parse(endTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return endTimeObject;
	}
	
	public BroadcastTypeEnum getBroadcastType() {
		return broadcastType;
	}
	public String getShareUrl() {
		return shareUrl;
	}
	
}
