
package com.millicom.mitv.models.gson;



import java.util.ArrayList;

import com.millicom.mitv.interfaces.GSONDataFieldValidation;
import com.millicom.mitv.models.TVBroadcastWithChannelInfo;



public class TVSearchResultEntity
	implements GSONDataFieldValidation
{
	protected String id;
	
	protected String name;
	
	protected ArrayList<TVBroadcastWithChannelInfo> broadcasts;
	
	
	
	public TVSearchResultEntity()
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
