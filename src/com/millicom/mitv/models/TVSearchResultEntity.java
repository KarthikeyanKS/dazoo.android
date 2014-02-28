package com.millicom.mitv.models;

import java.util.ArrayList;

import com.millicom.mitv.models.gson.TVSearchResultEntityJSON;

public class TVSearchResultEntity extends TVSearchResultEntityJSON {

	/* Constructor for entity if channel */
	public TVSearchResultEntity(TVChannel tvChannel)
	{
		this.tvChannel = tvChannel;
	}
	
	/* Constructor for entity if program */
	public TVSearchResultEntity(TVProgram tvProgram, ArrayList<TVBroadcastWithChannelInfo> broadcasts)
	{
		this.tvProgram = tvProgram;
		this.broadcasts = broadcasts;
	}
	
	/* Constructor for entity if series */
	public TVSearchResultEntity(String seriesId, String seriesName, ArrayList<TVBroadcastWithChannelInfo> broadcasts)
	{
		this.id = seriesId;
		this.name = seriesName;
		this.broadcasts = broadcasts;
	}
}
