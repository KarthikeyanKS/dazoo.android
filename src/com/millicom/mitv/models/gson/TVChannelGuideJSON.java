
package com.millicom.mitv.models.gson;



import java.util.ArrayList;

import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVChannel;



public class TVChannelGuideJSON 
	extends TVChannel
{
	private ArrayList<TVBroadcast> broadcasts;

	
	
	public ArrayList<TVBroadcast> getBroadcasts() 
	{
		return broadcasts;
	}
}