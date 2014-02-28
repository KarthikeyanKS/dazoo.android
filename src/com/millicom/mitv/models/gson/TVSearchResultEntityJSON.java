
package com.millicom.mitv.models.gson;



import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;
import com.millicom.mitv.models.TVChannel;
import com.millicom.mitv.models.TVProgram;



public class TVSearchResultEntityJSON
{
	/* USED BY BOTH SERIES AND PROGRAM */
	@Expose (deserialize = false)
	protected ArrayList<TVBroadcastWithChannelInfo> broadcasts;
	
	/* IF SERIES */
	@Expose (deserialize = false)
	protected String name;
	
	@Expose (deserialize = false)
	protected String id;

	/* IF PROGRAM */
	@Expose (deserialize = false)
	protected TVProgram tvProgram;
	
	/* IF CHANNEL */
	@Expose (deserialize = false)
	protected TVChannel tvChannel;
	
	public TVSearchResultEntityJSON()
	{}


	public String getId() {
		return id;
	}



	public String getName() {
		return name;
	}

	
	public TVChannel getChannel() {
		return tvChannel;
	}

	public TVProgram getProgram() {
		return tvProgram;
	}

	public ArrayList<TVBroadcastWithChannelInfo> getBroadcasts() {
		return broadcasts;
	}

}
