
package com.mitv.notification;



import java.io.Serializable;

import android.util.Log;

import com.millicom.mitv.enums.ProgramTypeEnum;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVProgram;



public class NotificationSQLElement
	implements Serializable
{
	private static final long serialVersionUID = 8910021727806717494L;


	private static final String	TAG	= NotificationSQLElement.class.getName();

	
	private int notificationId;
	
	private long broadcastBeginTimeInMilliseconds;
	private String broadcastBeginTime;
	private String broadcastEndTime;
	private String broadcastType;
	
	private String shareUrl;

	private String channelId;
	private String channelName;
	private String channelLogoSmall;
	private String channelLogoMedium;
	private String channelLogoLarge;
	
	private String programId;
	private String programTitle;
	private String programType;
	private String synopsisShort;
	private String synopsisLong;
	private String programTags;
	private String programCredits;
	
	private String programSeason;
	private int programEpisodeNumber;
	private int programYear;
	private String programGenre;
	private String programCategory;
	
	private String seriesId;
	private String seriesName;
	
	private String sportTypeId;
	private String sportTypeName;
	
	
	/* For use only in the NotificationDataSource class */
	public NotificationSQLElement()
	{}
	
	
	public NotificationSQLElement(
			int notificationId,
			TVBroadcastWithChannelInfo tvBroadcastWithChannelInfo)
	{
		this.notificationId = notificationId;
		
		this.broadcastBeginTimeInMilliseconds = tvBroadcastWithChannelInfo.getBeginTimeMillis().longValue();
		
		TVProgram tvProgram = tvBroadcastWithChannelInfo.getProgram();
		
		this.programId = tvProgram.getProgramId();
		
		this.programTitle = tvProgram.getTitle();
		this.synopsisShort = tvProgram.getSynopsisShort();
		this.synopsisLong = tvProgram.getSynopsisLong();
		
		this.programTags = tvProgram.getTags().toString();
		this.programCredits = tvProgram.getCredits().toString();
		
		ProgramTypeEnum programType = tvProgram.getProgramType();
		
		this.programType = programType.toString();
		
		switch(programType)
		{
			case TV_EPISODE:
			{
				this.seriesId = tvProgram.getSeries().getSeriesId();
				this.seriesName = tvProgram.getSeries().getName();
				
				this.programSeason = tvProgram.getSeason().getNumber().toString();
				this.programEpisodeNumber = tvProgram.getEpisodeNumber();
				break;
			}
			
			case SPORT:
			{
				this.sportTypeId = tvProgram.getSportType().getSportTypeId();
				this.sportTypeName = tvProgram.getSportType().getName();
				this.programGenre = tvProgram.getTournament();
				break;
			}
			
			case OTHER:
			{
				this.programCategory = tvProgram.getCategory();
				break;
			}
			
			case MOVIE:
			{
				this.programYear = tvProgram.getYear();
				this.programGenre = tvProgram.getGenre();
				break;
			}
			
			case UNKNOWN:
			default:
			{
				Log.w(TAG, "Unhandled program type.");
				break;
			}
		}

		this.channelName = tvBroadcastWithChannelInfo.getChannel().getName();
		this.channelId = tvBroadcastWithChannelInfo.getChannel().getChannelId().getChannelId();
		this.channelLogoSmall = tvBroadcastWithChannelInfo.getChannel().getLogo().getSmall();
		this.channelLogoMedium = tvBroadcastWithChannelInfo.getChannel().getLogo().getMedium();
		this.channelLogoLarge = tvBroadcastWithChannelInfo.getChannel().getLogo().getLarge();
		this.shareUrl = tvBroadcastWithChannelInfo.getShareUrl();		
		this.broadcastBeginTime = tvBroadcastWithChannelInfo.getBeginTime();
		this.broadcastEndTime = tvBroadcastWithChannelInfo.getEndTime();
		this.broadcastType = tvBroadcastWithChannelInfo.getBroadcastType().toString();
	}
	
	

	public int getNotificationId(){
		return notificationId;
	}
	
	
	public String getProgramId(){
		return this.programId;
	}
	
	
	public String getProgramTitle(){
		return this.programTitle;
	}
	
	
	public String getProgramType(){
		return this.programType;
	}
	

	public String getProgramSeason(){
		return this.programSeason;
	}
	
	
	public int getProgramEpisodeNumber(){
		return this.programEpisodeNumber;
	}
	

	public int getProgramYear(){
		return this.programYear;
	}
	
	public String getProgramGenre(){
		return this.programGenre;
	}
	
	
	public String getProgramCategory(){
		return this.programCategory;
	}
	
	
	public String getChannelId(){
		return this.channelId;
	}
	
	
	public String getChannelName(){
		return this.channelName;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	public static String getTag() {
		return TAG;
	}



	public String getBroadcastBeginTime() {
		return broadcastBeginTime;
	}



	public String getBroadcastEndTime() {
		return broadcastEndTime;
	}



	public String getBroadcastType() {
		return broadcastType;
	}



	public String getChannelLogoSmall() {
		return channelLogoSmall;
	}



	public String getChannelLogoMedium() {
		return channelLogoMedium;
	}



	public String getChannelLogoLarge() {
		return channelLogoLarge;
	}



	public String getSynopsisShort() {
		return synopsisShort;
	}



	public String getSynopsisLong() {
		return synopsisLong;
	}



	public String getProgramTags() {
		return programTags;
	}



	public String getProgramCredits() {
		return programCredits;
	}



	public String getSeriesId() {
		return seriesId;
	}



	public String getSeriesName() {
		return seriesName;
	}



	public String getSportTypeId() {
		return sportTypeId;
	}



	public String getSportTypeName() {
		return sportTypeName;
	}



	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}



	public void setBroadcastBeginTime(String broadcastBeginTime) {
		this.broadcastBeginTime = broadcastBeginTime;
	}



	public void setBroadcastEndTime(String broadcastEndTime) {
		this.broadcastEndTime = broadcastEndTime;
	}



	public void setBroadcastType(String broadcastType) {
		this.broadcastType = broadcastType;
	}


	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}



	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}



	public void setChannelLogoSmall(String channelLogoSmall) {
		this.channelLogoSmall = channelLogoSmall;
	}



	public void setChannelLogoMedium(String channelLogoMedium) {
		this.channelLogoMedium = channelLogoMedium;
	}



	public void setChannelLogoLarge(String channelLogoLarge) {
		this.channelLogoLarge = channelLogoLarge;
	}



	public void setProgramId(String programId) {
		this.programId = programId;
	}



	public void setProgramTitle(String programTitle) {
		this.programTitle = programTitle;
	}



	public void setProgramType(String programType) {
		this.programType = programType;
	}



	public void setSynopsisShort(String synopsisShort) {
		this.synopsisShort = synopsisShort;
	}



	public void setSynopsisLong(String synopsisLong) {
		this.synopsisLong = synopsisLong;
	}



	public void setProgramTags(String programTags) {
		this.programTags = programTags;
	}



	public void setProgramCredits(String programCredits) {
		this.programCredits = programCredits;
	}



	public void setProgramSeason(String programSeason) {
		this.programSeason = programSeason;
	}



	public void setProgramEpisodeNumber(int programEpisodeNumber) {
		this.programEpisodeNumber = programEpisodeNumber;
	}



	public void setProgramYear(int programYear) {
		this.programYear = programYear;
	}



	public void setProgramGenre(String programGenre) {
		this.programGenre = programGenre;
	}



	public void setProgramCategory(String programCategory) {
		this.programCategory = programCategory;
	}



	public void setSeriesId(String seriesId) {
		this.seriesId = seriesId;
	}



	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}



	public void setSportTypeId(String sportTypeId) {
		this.sportTypeId = sportTypeId;
	}



	public void setSportTypeName(String sportTypeName) {
		this.sportTypeName = sportTypeName;
	}
	
	
	public String getShareUrl() {
		return shareUrl;
	}


	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}


	public long getBroadcastBeginTimeInMilliseconds() {
		return broadcastBeginTimeInMilliseconds;
	}


	public void setBroadcastBeginTimeInMilliseconds(
			long broadcastBeginTimeInMilliseconds) {
		this.broadcastBeginTimeInMilliseconds = broadcastBeginTimeInMilliseconds;
	}
}
