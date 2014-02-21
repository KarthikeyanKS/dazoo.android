
package com.millicom.mitv.models.gson;



import java.util.ArrayList;

import com.millicom.mitv.interfaces.GSONDataFieldValidation;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;



public class TVSearchResultEntityJSON
	implements GSONDataFieldValidation
{
	protected String name;
	
	//TODO NewArc give support in this class for not only TVSeries but TVChannel and TVProgram as well
	/* IF SERIES */
//	@Expose (deserialize = false)
	protected String id;
	
//	@Expose (deserialize = false)
	protected ArrayList<TVBroadcastWithChannelInfo> broadcasts;
	
	
	
	public TVSearchResultEntityJSON()
	{}


	
	public String getId() {
		return id;
	}



	public String getName() {
		return name;
	}



	public ArrayList<TVBroadcastWithChannelInfo> getBroadcasts() {
		return broadcasts;
	}
	
	
	
	@Override
	public boolean areDataFieldsValid() 
	{
		boolean areFieldsValid = (name != null && name.length() > 0 && 
								  id != null && id.length() > 0);
		
		// TODO : Validate broadcasts
		
		return areFieldsValid;
	}
}
