
package com.millicom.mitv.models.gson;



import java.util.ArrayList;

import com.millicom.mitv.models.TVBroadcast;
import com.millicom.mitv.models.TVChannel;
import com.millicom.mitv.models.TVDate;



public class TVChannelGuide 
	extends TVChannel
{
	private ArrayList<TVBroadcast> broadcasts;

	
	
	public ArrayList<TVBroadcast> getBroadcasts() 
	{
		return broadcasts;
	}

	
	//TODO Determine which of those dummy methods we need, and implement them
	/* HERE COMES DUMMY METHODS, ALL OF THEM MAY NOT BE NEEDED, INVESTIGATE! */
	public int getClosestBroadcastIndexFromTime(ArrayList<TVBroadcast> broadcastList, int hour, TVDate date) 
	{
		//TODO implement or delete me
		return 0;
	}
}
