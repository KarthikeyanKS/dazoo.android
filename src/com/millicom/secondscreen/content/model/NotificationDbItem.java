package com.millicom.secondscreen.content.model;

public class NotificationDbItem {
	private int notificationId;
	private String broadcastUrl;
	private String programId;
	private String programTitle;
	private String programType;
	private String programSeason;
	private int programEpisodeNumber;
	private int programYear;
	private String programTag;
	private String programGenre;
	private String channelId;
	private String channelName;
	private String channelLogoUrl;
	private String broadcastBeginTime;
	private String broadcastBeginTimeMillis;
	
	public void setNotificationId(int id){
		this.notificationId = id;
	}

	public int getNotificationId(){
		return notificationId;
	}
	
	public void setBroadcstUrl(String broadcastUrl){
		this.broadcastUrl = broadcastUrl;
	}
	
	public String getBroadcastUrl(){
		return this.broadcastUrl;
	}
	
	public void setProgramId(String programId){
		this.programId = programId;
	}
	
	public String getProgramId(){
		return this.programId;
	}
	
	public void setProgramTitle(String programTitle){
		this.programTitle = programTitle;
	}
	
	public String getProgramTitle(){
		return this.programTitle;
	}
	
	public void setProgramType(String programType){
		this.programType = programType;
	}
	
	public String getProgramType(){
		return this.programType;
	}
	
	public void setProgramSeason(String programSeason){
		this.programSeason = programSeason;
	}
	
	public String getProgramSeason(){
		return this.programSeason;
	}
	
	public void setProgramEpisodeNumber(int programEpisodeNumber){
		this.programEpisodeNumber = programEpisodeNumber;
	}
	
	public int getProgramEpisodeNumber(){
		return this.programEpisodeNumber;
	}
	
	public void setProgramYear(int programYear){
		this.programYear = programYear;
	}
	
	public int getProgramYear(){
		return this.programYear;
	}
	
	public void setProgramTag(String programTag){
		this.programTag = programTag;
	}
	
	public String getProgramTag(){
		return this.programTag;
	}
	
	public void setProgramGenre(String programGenre){
		this.programGenre = programGenre;
	}
	
	public String getProgramGenre(){
		return this.programGenre;
	}
	
	
	public void setChannelId(String channelId){
		this.channelId = channelId;
	}
	
	public String getChannelId(){
		return this.channelId;
	}
	
	public void setChannelName(String channelName){
		this.channelName = channelName;
	}
	
	public String getChannelName(){
		return this.channelName;
	}
	
	public void setChannelLogoUrl(String channelLogoUrl){
		this.channelLogoUrl = channelLogoUrl;
	}
	
	public String getChannelLogoUrl(){
		return this.channelLogoUrl;
	}
	
	public void setBroadcastBeginTime(String broadcastBeginTime){
		this.broadcastBeginTime = broadcastBeginTime;
	}
	
	public String getBroadcastBeginTime(){
		return this.broadcastBeginTime;
	}
	
	public void setBroadcastBeginTimeMillis(String broadcastBeginTimeMillis){
		this.broadcastBeginTimeMillis = broadcastBeginTimeMillis;
	}
	
	public String getBroadcastTimeInMillis(){
		return this.broadcastBeginTimeMillis;
	}

}
