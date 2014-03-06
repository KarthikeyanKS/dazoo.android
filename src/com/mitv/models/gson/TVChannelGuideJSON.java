
package com.mitv.models.gson;



import java.util.ArrayList;

import com.mitv.models.TVBroadcast;
import com.mitv.models.TVChannel;



public class TVChannelGuideJSON 
	extends TVChannel
{
	protected ArrayList<TVBroadcast> broadcasts;

	
	
	public ArrayList<TVBroadcast> getBroadcasts() 
	{
		return broadcasts;
	}
}