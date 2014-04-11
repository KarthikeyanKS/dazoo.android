
package com.mitv.models.gson.mitvapi;



import java.util.ArrayList;

import com.mitv.models.objects.mitvapi.TVBroadcast;
import com.mitv.models.objects.mitvapi.TVChannel;



public class TVChannelGuideJSON 
	extends TVChannel
{
	protected ArrayList<TVBroadcast> broadcasts;

	
	
	public ArrayList<TVBroadcast> getBroadcasts() 
	{
		return broadcasts;
	}
}