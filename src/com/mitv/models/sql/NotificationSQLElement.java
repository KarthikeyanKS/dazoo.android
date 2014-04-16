
package com.mitv.models.sql;



import java.io.Serializable;

import android.database.Cursor;
import android.util.Log;

import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVProgram;



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
	private String programImageSmallLandscape;
	private String programImageMediumLandscape;
	private String programImageLargeLandscape;
	private String programImageSmallPortrait;
	private String programImageMediumPortrait;
	private String programImageLargePortrait;
	
	private String seriesId;
	private String seriesName;
	
	private String sportTypeId;
	private String sportTypeName;
	
	
	
	public NotificationSQLElement(Cursor cursor)
	{
		if (cursor.getCount() > 0)
		{
			if (cursor.isNull(0) == false)
			{
				this.setNotificationId(cursor.getInt(0));
				
				this.setBroadcastBeginTimeInMilliseconds(cursor.getLong(1));
				
				this.setBroadcastBeginTime(cursor.getString(2));
				
				this.setBroadcastEndTime(cursor.getString(3));
				
				this.setBroadcastType(cursor.getString(4));
				
				this.setShareUrl(cursor.getString(5));
				
				this.setChannelId(cursor.getString(6));
				
				this.setChannelName(cursor.getString(7));
				
				this.setChannelLogoSmall(cursor.getString(8));
				
				this.setChannelLogoMedium(cursor.getString(9));
				
				this.setChannelLogoLarge(cursor.getString(10));
				
				this.setProgramId(cursor.getString(11));
				
				this.setProgramTitle(cursor.getString(12));

				this.setProgramType(cursor.getString(13));
				
				this.setSynopsisShort(cursor.getString(14));
				
				this.setSynopsisLong(cursor.getString(15));
				
				this.setProgramTags(cursor.getString(16));
				
				this.setProgramCredits(cursor.getString(17));
				
				this.programImageSmallPortrait = cursor.getString(23);
				this.programImageMediumPortrait = cursor.getString(24);
				this.programImageLargePortrait = cursor.getString(25);
				this.programImageSmallLandscape = cursor.getString(26);
				this.programImageMediumLandscape = cursor.getString(27);
				this.programImageLargeLandscape = cursor.getString(28);
				
				String programTypeAsString = cursor.getString(13);
				
				ProgramTypeEnum programType = ProgramTypeEnum.getLikeTypeEnumFromStringRepresentation(programTypeAsString);
				
				switch(programType)
				{
					case TV_EPISODE:
					{
						this.setProgramSeason(cursor.getString(18));
						this.setProgramEpisodeNumber(cursor.getInt(19));
						this.setSeriesId(cursor.getString(29));
						this.setSeriesName(cursor.getString(30));
						break;
					}
					
					case SPORT:
					{
						this.setSportTypeId(cursor.getString(31));
						this.setSportTypeName(cursor.getString(32));
						this.setProgramGenre(cursor.getString(21));
						break;
					}
					
					case OTHER:
					{
						this.setProgramCategory(cursor.getString(22));
						break;
					}
					
					case MOVIE:
					{
						this.setProgramYear(cursor.getInt(20));
						this.setProgramGenre(cursor.getString(21));
						break;
					}
					
					case UNKNOWN:
					default:
					{
						Log.w(TAG, "Unhandled program type.");
						break;
					}
				}
			}
		}
	}
	
	
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
		
		this.programImageSmallLandscape = tvProgram.getImages().getLandscape().getSmall();
		this.programImageMediumLandscape = tvProgram.getImages().getLandscape().getMedium();
		this.programImageLargeLandscape = tvProgram.getImages().getLandscape().getLarge();
		this.programImageSmallPortrait = tvProgram.getImages().getPortrait().getSmall();
		this.programImageMediumPortrait = tvProgram.getImages().getPortrait().getMedium();
		this.programImageLargePortrait = tvProgram.getImages().getPortrait().getLarge();
		
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


	public String getProgramImageSmallLandscape() {
		return programImageSmallLandscape;
	}


	public void setProgramImageSmallLandscape(String programImageSmallLandscape) {
		this.programImageSmallLandscape = programImageSmallLandscape;
	}


	public String getProgramImageMediumLandscape() {
		return programImageMediumLandscape;
	}


	public void setProgramImageMediumLandscape(String programImageMediumLandscape) {
		this.programImageMediumLandscape = programImageMediumLandscape;
	}


	public String getProgramImageLargeLandscape() {
		return programImageLargeLandscape;
	}


	public void setProgramImageLargeLandscape(String programImageLargeLandscape) {
		this.programImageLargeLandscape = programImageLargeLandscape;
	}


	public String getProgramImageSmallPortrait() {
		return programImageSmallPortrait;
	}


	public void setProgramImageSmallPortrait(String programImageSmallPortrait) {
		this.programImageSmallPortrait = programImageSmallPortrait;
	}


	public String getProgramImageMediumPortrait() {
		return programImageMediumPortrait;
	}


	public void setProgramImageMediumPortrait(String programImageMediumPortrait) {
		this.programImageMediumPortrait = programImageMediumPortrait;
	}


	public String getProgramImageLargePortrait() {
		return programImageLargePortrait;
	}


	public void setProgramImageLargePortrait(String programImageLargePortrait) {
		this.programImageLargePortrait = programImageLargePortrait;
	}
}
