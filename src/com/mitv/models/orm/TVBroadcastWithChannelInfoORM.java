
package com.mitv.models.orm;



import java.util.ArrayList;

import com.j256.ormlite.field.DatabaseField;
import com.mitv.enums.BroadcastTypeEnum;
import com.mitv.enums.ProgramTypeEnum;
import com.mitv.models.objects.mitvapi.ImageSetSize;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVCredit;
import com.mitv.models.objects.mitvapi.TVProgram;
import com.mitv.models.orm.base.AbstractOrmLiteClassWithAsyncSave;



public class TVBroadcastWithChannelInfoORM 
	extends AbstractOrmLiteClassWithAsyncSave<TVBroadcastWithChannelInfoORM> 
{	
	/* TVChannel */
	@DatabaseField()
	protected String channelId;
	
	@DatabaseField()
	protected String channelName;
	
	
	/* TVChannel - ImageSetSize */
	@DatabaseField()
	protected String smallLogo;
	
	@DatabaseField()
	protected String mediumLogo;
	
	@DatabaseField()
	protected String largeLogo;
	
	
	
	/* TVProgram */
	@DatabaseField()
	protected ProgramTypeEnum programType;
	
	@DatabaseField()
	protected String programId;
	
	@DatabaseField()
	protected String title;
	
	@DatabaseField()
	protected String synopsisShort;
	
	@DatabaseField()
	protected String synopsisLong;
	
	@DatabaseField()
	protected String smallLandscape;
	
	@DatabaseField()
	protected String mediumLandscape;
	
	@DatabaseField()
	protected String largeLandscape;
	
	@DatabaseField()
	protected String smallPortrait;
	
	@DatabaseField()
	protected String mediumPortrait;
	
	@DatabaseField()
	protected String largePortrait;	
	
	@DatabaseField()
	protected ArrayList<String> tags;
	
	@DatabaseField()
	protected ArrayList<TVCredit> credits;
	
	/* This variable is used if programType == "OTHER" */
	@DatabaseField()
	protected String category; 
	
	/* The following variables are being used if programType == "TV_EPISODE" */
	@DatabaseField()
	protected String seriesId;
	
	@DatabaseField()
	protected String seriesName;
	
	@DatabaseField()
	protected Integer number;

	@DatabaseField()
	protected Integer episodeNumber;
	
	/* The following variables are being used if programType == "MOVIE" */
	@DatabaseField()
	protected Integer year;
	
	@DatabaseField()
	protected String genre;
	
	/* The following variables are being used if programType == "SPORT" */
	@DatabaseField()
	protected String sportTypeId;
	
	@DatabaseField()
	protected String sportTypeName;

	@DatabaseField()
	protected String tournament;
	
	
	
	/* TVBroadcast */
	@DatabaseField()
	protected Long beginTimeMillis;
	
	@DatabaseField()
	protected String beginTime;
	
	@DatabaseField()
	protected String endTime;
	
	@DatabaseField()
	protected BroadcastTypeEnum broadcastType;
	
	@DatabaseField()
	protected String shareUrl;	
	
	
	
	public TVBroadcastWithChannelInfoORM(TVBroadcastWithChannelInfo tvBroadcastWithChannelInfo) 
	{	
		/* TVChannel */
		this.channelId = tvBroadcastWithChannelInfo.getChannel().getChannelId().getChannelId();
		this.channelName = tvBroadcastWithChannelInfo.getChannel().getName();
		
		/* TVChannel - ImageSetSize */
		ImageSetSize getLogo = tvBroadcastWithChannelInfo.getChannel().getLogo();
		this.smallLogo = getLogo.getSmall();
		this.mediumLogo = getLogo.getMedium();
		this.largeLogo = getLogo.getLarge();
		
		
		/* TVProgram */
		TVProgram tvProgram = tvBroadcastWithChannelInfo.getProgram();
		this.programType = tvProgram.getProgramType();
		
		this.programId = tvProgram.getProgramId();
		this.title = tvProgram.getTitle();
		this.synopsisShort = tvProgram.getSynopsisShort();
		this.synopsisLong = tvProgram.getSynopsisLong();
		
		ImageSetSize imageLandscape = tvProgram.getImages().getLandscape();
		this.smallLandscape = imageLandscape.getSmall();
		this.mediumLandscape = imageLandscape.getMedium();
		this.largeLandscape = imageLandscape.getLarge();
		
		ImageSetSize imagePortrait = tvProgram.getImages().getPortrait();
		this.smallPortrait = imagePortrait.getSmall();
		this.mediumPortrait = imagePortrait.getMedium();
		this.largePortrait = imagePortrait.getLarge();
		
		this.tags = tvProgram.getTags();
		this.credits = tvProgram.getCredits();
		this.category = tvProgram.getCategory();
		
		this.seriesId = tvProgram.getSeries().getSeriesId();
		this.seriesName = tvProgram.getSeries().getName();
		
		this.number = tvProgram.getSeason().getNumber();
		
		this.episodeNumber = tvProgram.getEpisodeNumber();
		this.year = tvProgram.getYear();
		this.genre = tvProgram.getGenre();
		
		this.sportTypeId = tvProgram.getSportType().getSportTypeId();
		this.sportTypeName = tvProgram.getSportType().getName();
		
		this.tournament = tvProgram.getTournament();
		
		
		/* TVBroadcast */
		this.beginTimeMillis = tvBroadcastWithChannelInfo.getBeginTimeMillis();
		this.beginTime = tvBroadcastWithChannelInfo.getBeginTime();
		this.endTime = tvBroadcastWithChannelInfo.getEndTime();
		this.broadcastType = tvBroadcastWithChannelInfo.getBroadcastType();
		this.shareUrl = tvBroadcastWithChannelInfo.getShareUrl();
	}
}