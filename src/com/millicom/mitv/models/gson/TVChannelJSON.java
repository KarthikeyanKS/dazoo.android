
package com.millicom.mitv.models.gson;



import com.millicom.mitv.models.ImageSetSize;
import com.millicom.mitv.models.TVChannelId;



public class TVChannelJSON
{
	protected String channelId;
	protected String name;
	protected ImageSetSize logo;
	
	protected transient TVChannelId tvChannelIdObject;
	
	
	
	public TVChannelId getChannelId() 
	{
		if(tvChannelIdObject == null) 
		{
			tvChannelIdObject = new TVChannelId(channelId);
		}
		
		return tvChannelIdObject;
	}
	
	
	
	public String getName() 
	{
		return name;
	}

	
	
	public ImageSetSize getLogo() 
	{
		return logo;
	}


	// TODO NewArc -  Replace with an implementation that takes the screen resolution into account
	/* Partially implemented method */
	public String getImageUrl()
	{
		// Should we use getImageURLForDensityDPI instead?
		//String imageUrl = logo.getImageURLForDensityDPI(densityDpi)
		
		return logo.getLarge();
	}
}
