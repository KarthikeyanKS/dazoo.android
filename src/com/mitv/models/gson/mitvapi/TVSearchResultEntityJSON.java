
package com.mitv.models.gson.mitvapi;



import java.util.Collections;
import java.util.List;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.mitv.models.gson.mitvapi.base.BaseObjectJSON;
import com.mitv.models.objects.mitvapi.TVBroadcastWithChannelInfo;
import com.mitv.models.objects.mitvapi.TVChannel;
import com.mitv.models.objects.mitvapi.TVProgram;



public class TVSearchResultEntityJSON
	extends BaseObjectJSON
{
	private static final String TAG = TVSearchResultEntityJSON.class.getName();
	
	
	/* USED BY BOTH SERIES AND PROGRAM */
	@Expose (deserialize = false)
	protected List<TVBroadcastWithChannelInfo> broadcasts;
	
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
	
	
	
	public TVSearchResultEntityJSON(){}


	
	public String getId() 
	{
		if(id == null)
		{
			id = "";
			
			Log.w(TAG, "id is null");
		}
		
		return id;
	}



	public String getName() 
	{
		if(name == null)
		{
			name = "";
			
			Log.w(TAG, "name is null");
		}
		
		return name;
	}

	
	
	public TVChannel getChannel()
	{
		if(tvChannel == null)
		{
			tvChannel = new TVChannel();
			
			Log.w(TAG, "tvChannel is null");
		}
		
		return tvChannel;
	}

	
	
	public TVProgram getProgram() 
	{
		if(tvProgram == null)
		{
			tvProgram = new TVProgram();
			
			Log.w(TAG, "tvProgram is null");
		}
		
		return tvProgram;
	}

	
	
	public List<TVBroadcastWithChannelInfo> getBroadcasts() 
	{
		if(broadcasts == null)
		{
			broadcasts = Collections.emptyList();
			
			Log.w(TAG, "broadcasts is null");
		}
		
		return broadcasts;
	}
}
